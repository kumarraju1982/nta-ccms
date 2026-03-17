package org.nta.ccms.auth.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  @PostMapping("/auth/login/password")
  public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
    String username = payload.getOrDefault("username", "unknown");
    return ResponseEntity.ok(Map.of(
        "token", "mock-token-" + username,
        "user", Map.of(
            "username", username,
            "role", "CALL_AGENT",
            "displayName", "CCMS User"
        )
    ));
  }

  @GetMapping("/users/me")
  public ResponseEntity<Map<String, Object>> me() {
    return ResponseEntity.ok(Map.of(
        "username", "agent1",
        "displayName", "Agent One",
        "role", "CALL_AGENT",
        "tenantId", "nta.default"
    ));
  }
}
