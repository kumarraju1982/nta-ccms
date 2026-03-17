package org.nta.ccms.ticket.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.nta.ccms.ticket.api.AssignTicketRequest;
import org.nta.ccms.ticket.api.CreateTicketRequest;
import org.nta.ccms.ticket.api.TicketHistoryResponse;
import org.nta.ccms.ticket.api.TicketResponse;
import org.nta.ccms.ticket.api.TransitionTicketRequest;
import org.nta.ccms.ticket.service.TicketDomainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1")
public class TicketController {
  private final TicketDomainService ticketDomainService;

  public TicketController(TicketDomainService ticketDomainService) {
    this.ticketDomainService = ticketDomainService;
  }

  @GetMapping("/tickets")
  public ResponseEntity<List<TicketResponse>> list() {
    return ResponseEntity.ok(ticketDomainService.list());
  }

  @PostMapping("/tickets")
  public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
    return ResponseEntity.ok(ticketDomainService.create(request));
  }

  @PostMapping("/tickets/{grievanceId}/assign")
  public ResponseEntity<TicketResponse> assign(@PathVariable String grievanceId, @RequestBody AssignTicketRequest request) {
    return ResponseEntity.ok(ticketDomainService.assign(grievanceId, request));
  }

  @PostMapping("/tickets/{grievanceId}/transition")
  public ResponseEntity<TicketResponse> transition(@PathVariable String grievanceId, @Valid @RequestBody TransitionTicketRequest request) {
    return ResponseEntity.ok(ticketDomainService.transition(grievanceId, request));
  }

  @GetMapping("/tickets/{grievanceId}/history")
  public ResponseEntity<List<TicketHistoryResponse>> history(@PathVariable String grievanceId) {
    return ResponseEntity.ok(ticketDomainService.history(grievanceId));
  }

  @GetMapping("/tickets/officer-queue")
  public ResponseEntity<List<TicketResponse>> officerQueue() {
    return ResponseEntity.ok(ticketDomainService.officerQueue());
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("ok");
  }
}
