package org.nta.ccms.ticket.api;

public record AssignTicketRequest(
    String assignedAgent,
    String assignedOfficer,
    String actionBy,
    String remarks
) {}
