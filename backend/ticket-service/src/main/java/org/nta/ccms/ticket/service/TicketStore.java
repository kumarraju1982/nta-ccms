package org.nta.ccms.ticket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class TicketStore {
  private final List<TicketRecord> tickets = new CopyOnWriteArrayList<>();

  public TicketStore() {
    tickets.add(new TicketRecord(
        "GRV-2026-0001",
        "Kavya Singh",
        "9876543210",
        "NEET_UG",
        "ADMIT_CARD",
        "DOWNLOAD_ISSUE",
        "NEW",
        null
    ));
  }

  public List<TicketRecord> findAll() {
    return new ArrayList<>(tickets);
  }

  public TicketRecord create(CreateTicketRequest request) {
    String grievanceId = "GRV-2026-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    TicketRecord created = new TicketRecord(
        grievanceId,
        request.candidateName(),
        request.candidateMobile(),
        request.examCode(),
        request.category(),
        request.subCategory(),
        "NEW",
        null
    );
    tickets.add(created);
    return created;
  }

  public record CreateTicketRequest(
      String candidateName,
      String candidateMobile,
      String examCode,
      String category,
      String subCategory
  ) {}

  public record TicketRecord(
      String grievanceId,
      String candidateName,
      String candidateMobile,
      String examCode,
      String category,
      String subCategory,
      String status,
      String assignedTo
  ) {}
}
