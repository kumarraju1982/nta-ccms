package org.nta.ccms.ticket.api;

public record TicketHistoryResponse(
    String fromStatus,
    String toStatus,
    String actionType,
    String actionBy,
    String remarks,
    String createdAt
) {}
