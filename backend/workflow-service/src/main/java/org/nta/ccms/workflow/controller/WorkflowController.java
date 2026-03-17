package org.nta.ccms.workflow.controller;

import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowController {

  private static final Set<String> VALID_STATUSES = Set.of(
      "NEW",
      "IN_PROGRESS",
      "PENDING_INFO_FROM_CANDIDATE",
      "RESOLVED_BY_AGENT",
      "UNRESOLVED",
      "ESCALATED_TO_OFFICER",
      "UNDER_OFFICER_REVIEW",
      "FINAL_RESOLVED",
      "REOPENED",
      "CLOSED"
  );

  @PostMapping("/tickets/{grievanceId}/transition")
  public ResponseEntity<Map<String, String>> transition(
      @PathVariable String grievanceId,
      @RequestBody Map<String, String> request
  ) {
    String toStatus = request.getOrDefault("toStatus", "");
    if (!VALID_STATUSES.contains(toStatus)) {
      return ResponseEntity.badRequest().body(Map.of(
          "error", "Invalid status",
          "grievanceId", grievanceId
      ));
    }
    return ResponseEntity.ok(Map.of(
        "grievanceId", grievanceId,
        "toStatus", toStatus,
        "result", "accepted"
    ));
  }
}
