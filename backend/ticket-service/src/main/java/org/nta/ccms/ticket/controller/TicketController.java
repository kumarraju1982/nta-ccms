package org.nta.ccms.ticket.controller;

import java.util.List;
import org.nta.ccms.ticket.service.TicketStore;
import org.nta.ccms.ticket.service.TicketStore.CreateTicketRequest;
import org.nta.ccms.ticket.service.TicketStore.TicketRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TicketController {
  private final TicketStore ticketStore;

  public TicketController(TicketStore ticketStore) {
    this.ticketStore = ticketStore;
  }

  @GetMapping("/tickets")
  public ResponseEntity<List<TicketRecord>> list() {
    return ResponseEntity.ok(ticketStore.findAll());
  }

  @PostMapping("/tickets")
  public ResponseEntity<TicketRecord> create(@RequestBody CreateTicketRequest request) {
    return ResponseEntity.ok(ticketStore.create(request));
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("ok");
  }
}
