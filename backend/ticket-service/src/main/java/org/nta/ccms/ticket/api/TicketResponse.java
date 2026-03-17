package org.nta.ccms.ticket.api;

public record TicketResponse(
    String grievanceId,
    String candidateName,
    String candidateMobile,
    String examCode,
    String category,
    String subCategory,
    String status,
    String assignedAgent,
    String assignedOfficer,
    String sourceChannel,
    int reopenCount,
    String updatedAt
) {}
