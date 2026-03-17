package org.nta.ccms.workflow.service;

import java.util.Map;
import org.nta.ccms.workflow.config.WorkflowProxyProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class DigitWorkflowProxyClient {
  private final RestTemplate restTemplate = new RestTemplate();
  private final WorkflowProxyProperties properties;

  public DigitWorkflowProxyClient(WorkflowProxyProperties properties) {
    this.properties = properties;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> transition(Map<String, Object> payload) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Tenant-ID", properties.getTenantId());
    headers.set("X-Client-Id", "nta-ccms");
    headers.setContentType(MediaType.APPLICATION_JSON);
    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(resolveUrl(properties.getTransitionPath()), new HttpEntity<>(payload, headers), Map.class);
      return response.getBody() == null ? Map.of() : response.getBody();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT workflow proxy failed: " + ex.getMessage());
    }
  }

  private String resolveUrl(String path) {
    String base = String.valueOf(properties.getBaseUrl() == null ? "" : properties.getBaseUrl()).replaceAll("/$", "");
    String suffix = String.valueOf(path == null ? "" : path);
    if (!suffix.startsWith("/")) {
      suffix = "/" + suffix;
    }
    return base + suffix;
  }
}
