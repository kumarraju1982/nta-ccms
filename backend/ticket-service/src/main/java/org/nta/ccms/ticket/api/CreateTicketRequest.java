package org.nta.ccms.ticket.api;

import jakarta.validation.constraints.NotBlank;

public record CreateTicketRequest(
    @NotBlank String candidateName,
    @NotBlank String candidateMobile,
    @NotBlank String examCode,
    @NotBlank String category,
    @NotBlank String subCategory,
    String sourceChannel
) {}
