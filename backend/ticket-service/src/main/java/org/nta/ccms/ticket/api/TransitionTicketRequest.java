package org.nta.ccms.ticket.api;

import jakarta.validation.constraints.NotBlank;

public record TransitionTicketRequest(
    @NotBlank String toStatus,
    @NotBlank String actionBy,
    String remarks
) {}
