package org.nta.ccms.auth.controller;

import java.util.Map;
import jakarta.validation.Valid;
import org.nta.ccms.auth.api.LoginRequest;
import org.nta.ccms.auth.service.DigitAuthClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
  private final DigitAuthClient digitAuthClient;

  public AuthController(DigitAuthClient digitAuthClient) {
    this.digitAuthClient = digitAuthClient;
  }

  @PostMapping("/auth/login/password")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest payload) {
    Map<String, Object> token = digitAuthClient.passwordLogin(payload.username(), payload.password());
    String accessToken = String.valueOf(token.get("access_token"));
    Map<String, Object> userInfo = digitAuthClient.userInfo(accessToken);
    return ResponseEntity.ok(digitAuthClient.normalizeLoginResponse(token, userInfo));
  }

  @GetMapping("/users/me")
  public ResponseEntity<Map<String, Object>> me(@RequestHeader(name = "Authorization", required = false) String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return ResponseEntity.status(401).body(Map.of("error", "Missing bearer token"));
    }
    String token = authorization.substring("Bearer ".length()).trim();
    return ResponseEntity.ok(digitAuthClient.userInfo(token));
  }
}
