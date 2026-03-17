package org.nta.ccms.common.dto;

public record TicketDto(
    String grievanceId,
    String candidateName,
    String candidateMobile,
    String examCode,
    String category,
    String subCategory,
    String status,
    String assignedTo
) {}
