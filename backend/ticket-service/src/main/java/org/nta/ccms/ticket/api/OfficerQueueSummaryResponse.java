package org.nta.ccms.ticket.api;

public record OfficerQueueSummaryResponse(
    long total,
    long unresolved,
    long escalatedToOfficer,
    long underOfficerReview,
    long reopened
) {}
