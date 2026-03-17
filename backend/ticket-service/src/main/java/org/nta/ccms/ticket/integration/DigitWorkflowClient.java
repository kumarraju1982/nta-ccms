package org.nta.ccms.ticket.integration;

import java.util.List;
import java.util.Map;
import org.nta.ccms.ticket.config.DigitIntegrationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class DigitWorkflowClient {
  private final RestTemplate restTemplate = new RestTemplate();
  private final DigitIntegrationProperties properties;

  public DigitWorkflowClient(DigitIntegrationProperties properties) {
    this.properties = properties;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> initWorkflow(String entityId) {
    String processId = resolveProcessId();
    Map<String, Object> body = Map.of(
        "processId", processId,
        "entityId", entityId,
        "init", true
    );
    return postTransition(body);
  }

  public Map<String, Object> transitionWorkflow(String processId, String processInstanceId, String entityId, String action) {
    Map<String, Object> body = Map.of(
        "processId", processId,
        "processInstanceId", processInstanceId,
        "entityId", entityId,
        "action", action
    );
    return postTransition(body);
  }

  @SuppressWarnings("unchecked")
  private String resolveProcessId() {
    String processCode = properties.getWorkflow().getProcessCode();
    if (processCode == null || processCode.isBlank()) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing ccms.digit.workflow.process-code configuration");
    }

    String url = resolveUrl(properties.getWorkflow().getProcessPath()) + "?code=" + processCode;
    HttpHeaders headers = baseHeaders();
    try {
      ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);
      List<?> processes = response.getBody();
      if (processes == null || processes.isEmpty() || !(processes.get(0) instanceof Map<?, ?> first)) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT workflow process lookup returned empty result for code " + processCode);
      }
      Object id = first.get("id");
      if (id == null || String.valueOf(id).isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT workflow process lookup missing id for code " + processCode);
      }
      return String.valueOf(id);
    } catch (ResponseStatusException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT workflow process lookup failed: " + ex.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> postTransition(Map<String, Object> body) {
    String url = resolveUrl(properties.getWorkflow().getTransitionPath());
    HttpHeaders headers = baseHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
      return response.getBody() == null ? Map.of() : response.getBody();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT workflow transition failed: " + ex.getMessage());
    }
  }

  private HttpHeaders baseHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Tenant-ID", properties.getTenantId());
    headers.set("X-Client-Id", "nta-ccms");
    return headers;
  }

  private String resolveUrl(String path) {
    String base = String.valueOf(properties.getWorkflow().getBaseUrl() == null ? "" : properties.getWorkflow().getBaseUrl()).replaceAll("/$", "");
    String suffix = String.valueOf(path == null ? "" : path);
    if (!suffix.startsWith("/")) {
      suffix = "/" + suffix;
    }
    return base + suffix;
  }
}
