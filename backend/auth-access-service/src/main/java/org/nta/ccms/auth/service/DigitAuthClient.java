package org.nta.ccms.auth.service;

import java.util.HashMap;
import java.util.Map;
import org.nta.ccms.auth.config.DigitAuthProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class DigitAuthClient {
  private final RestTemplate restTemplate = new RestTemplate();
  private final DigitAuthProperties properties;

  public DigitAuthClient(DigitAuthProperties properties) {
    this.properties = properties;
  }

  public Map<String, Object> passwordLogin(String username, String password) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "password");
    body.add("username", username);
    body.add("password", password);
    body.add("client_id", properties.getClientId());
    body.add("scope", "openid");
    if (properties.getClientSecret() != null && !properties.getClientSecret().isBlank()) {
      body.add("client_secret", properties.getClientSecret());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    ResponseEntity<Map> tokenResponse;
    try {
      tokenResponse = restTemplate.postForEntity(resolveUrl(properties.getTokenPath()), new HttpEntity<>(body, headers), Map.class);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT auth token endpoint failed: " + ex.getMessage());
    }

    Map<String, Object> token = tokenResponse.getBody();
    if (token == null || token.get("access_token") == null) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT auth token response missing access_token");
    }
    return token;
  }

  public Map<String, Object> userInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setAccept(MediaType.parseMediaTypes("application/json"));
    try {
      ResponseEntity<Map> response = restTemplate.exchange(
          resolveUrl(properties.getUserInfoPath()),
          HttpMethod.GET,
          new HttpEntity<>(headers),
          Map.class
      );
      if (response.getBody() == null) {
        return Map.of();
      }
      return response.getBody();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT auth userinfo endpoint failed: " + ex.getMessage());
    }
  }

  public Map<String, Object> normalizeLoginResponse(Map<String, Object> token, Map<String, Object> userInfo) {
    String username = stringOr(userInfo.get("preferred_username"), "unknown");
    String name = stringOr(userInfo.get("name"), username);
    String role = "CALL_AGENT";
    Object roles = userInfo.get("roles");
    if (roles instanceof String rs && !rs.isBlank()) {
      role = rs;
    }

    Map<String, Object> user = new HashMap<>();
    user.put("username", username);
    user.put("displayName", name);
    user.put("role", role);
    user.put("email", stringOr(userInfo.get("email"), ""));

    Map<String, Object> result = new HashMap<>();
    result.put("token", token.get("access_token"));
    result.put("refreshToken", token.get("refresh_token"));
    result.put("expiresIn", token.get("expires_in"));
    result.put("user", user);
    return result;
  }

  private String resolveUrl(String path) {
    String base = String.valueOf(properties.getBaseUrl() == null ? "" : properties.getBaseUrl()).replaceAll("/$", "");
    String suffix = String.valueOf(path == null ? "" : path);
    if (!suffix.startsWith("/")) {
      suffix = "/" + suffix;
    }
    return base + suffix;
  }

  private String stringOr(Object value, String fallback) {
    return value == null ? fallback : String.valueOf(value);
  }
}
