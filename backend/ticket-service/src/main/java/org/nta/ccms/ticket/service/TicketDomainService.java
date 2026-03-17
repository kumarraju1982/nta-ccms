package org.nta.ccms.ticket.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.nta.ccms.ticket.api.AssignTicketRequest;
import org.nta.ccms.ticket.api.CreateTicketRequest;
import org.nta.ccms.ticket.api.TicketHistoryResponse;
import org.nta.ccms.ticket.api.TicketResponse;
import org.nta.ccms.ticket.api.TransitionTicketRequest;
import org.nta.ccms.ticket.integration.DigitNotificationClient;
import org.nta.ccms.ticket.integration.DigitWorkflowClient;
import org.nta.ccms.ticket.domain.TicketEntity;
import org.nta.ccms.ticket.domain.TicketHistoryEntity;
import org.nta.ccms.ticket.domain.TicketStatus;
import org.nta.ccms.ticket.repository.TicketHistoryRepository;
import org.nta.ccms.ticket.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TicketDomainService {
  private static final Set<TicketStatus> OFFICER_QUEUE_STATUSES = Set.of(
      TicketStatus.UNRESOLVED,
      TicketStatus.ESCALATED_TO_OFFICER,
      TicketStatus.UNDER_OFFICER_REVIEW,
      TicketStatus.REOPENED
  );

  private static final Map<TicketStatus, Set<TicketStatus>> VALID_TRANSITIONS = Map.of(
      TicketStatus.NEW, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.UNRESOLVED, TicketStatus.CLOSED),
      TicketStatus.IN_PROGRESS, Set.of(TicketStatus.PENDING_INFO_FROM_CANDIDATE, TicketStatus.RESOLVED_BY_AGENT, TicketStatus.UNRESOLVED),
      TicketStatus.PENDING_INFO_FROM_CANDIDATE, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.UNRESOLVED),
      TicketStatus.UNRESOLVED, Set.of(TicketStatus.ESCALATED_TO_OFFICER),
      TicketStatus.ESCALATED_TO_OFFICER, Set.of(TicketStatus.UNDER_OFFICER_REVIEW),
      TicketStatus.UNDER_OFFICER_REVIEW, Set.of(TicketStatus.FINAL_RESOLVED, TicketStatus.UNRESOLVED),
      TicketStatus.RESOLVED_BY_AGENT, Set.of(TicketStatus.CLOSED, TicketStatus.REOPENED),
      TicketStatus.FINAL_RESOLVED, Set.of(TicketStatus.CLOSED, TicketStatus.REOPENED),
      TicketStatus.CLOSED, Set.of(TicketStatus.REOPENED),
      TicketStatus.REOPENED, Set.of(TicketStatus.IN_PROGRESS)
  );

  private final TicketRepository ticketRepository;
  private final TicketHistoryRepository ticketHistoryRepository;
  private final DigitWorkflowClient digitWorkflowClient;
  private final DigitNotificationClient digitNotificationClient;

  public TicketDomainService(
      TicketRepository ticketRepository,
      TicketHistoryRepository ticketHistoryRepository,
      DigitWorkflowClient digitWorkflowClient,
      DigitNotificationClient digitNotificationClient
  ) {
    this.ticketRepository = ticketRepository;
    this.ticketHistoryRepository = ticketHistoryRepository;
    this.digitWorkflowClient = digitWorkflowClient;
    this.digitNotificationClient = digitNotificationClient;
  }

  @Transactional(readOnly = true)
  public List<TicketResponse> list() {
    return ticketRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<TicketResponse> officerQueue() {
    return ticketRepository.findByStatusInOrderByUpdatedAtDesc(OFFICER_QUEUE_STATUSES.stream().toList()).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public TicketResponse create(CreateTicketRequest request) {
    TicketEntity entity = new TicketEntity();
    entity.setGrievanceId(nextGrievanceId());
    entity.setCandidateName(request.candidateName());
    entity.setCandidateMobile(request.candidateMobile());
    entity.setExamCode(request.examCode());
    entity.setCategory(request.category());
    entity.setSubCategory(request.subCategory());
    entity.setStatus(TicketStatus.NEW);
    entity.setSourceChannel(request.sourceChannel() == null || request.sourceChannel().isBlank() ? "CALL" : request.sourceChannel());
    entity.setReopenCount(0);
    OffsetDateTime now = OffsetDateTime.now();
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);

    Map<String, Object> workflow = digitWorkflowClient.initWorkflow(entity.getGrievanceId());
    entity.setWorkflowProcessId(stringOr(workflow.get("processId"), ""));
    entity.setWorkflowInstanceId(stringOr(workflow.get("id"), ""));

    TicketEntity saved = ticketRepository.save(entity);
    writeHistory(saved, "NA", saved.getStatus().name(), "CREATED", "SYSTEM", "Ticket created");
    digitNotificationClient.sendTicketStatusUpdate(saved.getCandidateMobile(), saved.getGrievanceId(), saved.getStatus().name());
    return toResponse(saved);
  }

  @Transactional
  public TicketResponse assign(String grievanceId, AssignTicketRequest request) {
    TicketEntity ticket = requireTicket(grievanceId);
    ticket.setAssignedAgent(request.assignedAgent());
    ticket.setAssignedOfficer(request.assignedOfficer());
    ticket.setUpdatedAt(OffsetDateTime.now());
    TicketEntity saved = ticketRepository.save(ticket);
    writeHistory(saved, saved.getStatus().name(), saved.getStatus().name(), "ASSIGNED",
        emptyTo(request.actionBy(), "SYSTEM"), request.remarks());
    return toResponse(saved);
  }

  @Transactional
  public TicketResponse transition(String grievanceId, TransitionTicketRequest request) {
    TicketEntity ticket = requireTicket(grievanceId);
    TicketStatus from = ticket.getStatus();
    TicketStatus to;
    try {
      to = TicketStatus.valueOf(request.toStatus());
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported status: " + request.toStatus());
    }

    if (!VALID_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transition from " + from + " to " + to);
    }

    String workflowAction = request.toStatus();

    if (ticket.getWorkflowProcessId() == null || ticket.getWorkflowProcessId().isBlank()
        || ticket.getWorkflowInstanceId() == null || ticket.getWorkflowInstanceId().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket is missing workflow linkage");
    }

    digitWorkflowClient.transitionWorkflow(
        ticket.getWorkflowProcessId(),
        ticket.getWorkflowInstanceId(),
        ticket.getGrievanceId(),
        workflowAction
    );

    if (to == TicketStatus.REOPENED) {
      ticket.setReopenCount(ticket.getReopenCount() + 1);
    }

    ticket.setStatus(to);
    ticket.setUpdatedAt(OffsetDateTime.now());
    TicketEntity saved = ticketRepository.save(ticket);
    writeHistory(saved, from.name(), to.name(), "TRANSITION", request.actionBy(), request.remarks());
    digitNotificationClient.sendTicketStatusUpdate(saved.getCandidateMobile(), saved.getGrievanceId(), saved.getStatus().name());
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<TicketHistoryResponse> history(String grievanceId) {
    requireTicket(grievanceId);
    return ticketHistoryRepository.findByGrievanceIdOrderByCreatedAtAsc(grievanceId).stream()
        .map(item -> new TicketHistoryResponse(
            item.getFromStatus(),
            item.getToStatus(),
            item.getActionType(),
            item.getActionBy(),
            item.getRemarks(),
            item.getCreatedAt().toString()
        ))
        .toList();
  }

  private TicketEntity requireTicket(String grievanceId) {
    return ticketRepository.findByGrievanceId(grievanceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + grievanceId));
  }

  private void writeHistory(TicketEntity ticket, String fromStatus, String toStatus, String actionType, String actionBy, String remarks) {
    TicketHistoryEntity history = new TicketHistoryEntity();
    history.setTicketId(ticket.getId());
    history.setGrievanceId(ticket.getGrievanceId());
    history.setFromStatus(fromStatus);
    history.setToStatus(toStatus);
    history.setActionType(actionType);
    history.setActionBy(actionBy);
    history.setRemarks(remarks);
    history.setCreatedAt(OffsetDateTime.now());
    ticketHistoryRepository.save(history);
  }

  private TicketResponse toResponse(TicketEntity item) {
    return new TicketResponse(
        item.getGrievanceId(),
        item.getCandidateName(),
        item.getCandidateMobile(),
        item.getExamCode(),
        item.getCategory(),
        item.getSubCategory(),
        item.getStatus().name(),
        item.getAssignedAgent(),
        item.getAssignedOfficer(),
        item.getSourceChannel(),
        item.getReopenCount(),
        item.getUpdatedAt().toString()
    );
  }

  private String nextGrievanceId() {
    return "GRV-" + OffsetDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
  }

  private String emptyTo(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private String stringOr(Object value, String fallback) {
    return value == null ? fallback : String.valueOf(value);
  }
}
