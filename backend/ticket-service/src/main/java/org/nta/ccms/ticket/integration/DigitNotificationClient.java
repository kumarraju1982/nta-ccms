package org.nta.ccms.ticket.integration;

import java.util.List;
import java.util.Map;
import org.nta.ccms.ticket.config.DigitIntegrationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class DigitNotificationClient {
  private final RestTemplate restTemplate = new RestTemplate();
  private final DigitIntegrationProperties properties;

  public DigitNotificationClient(DigitIntegrationProperties properties) {
    this.properties = properties;
  }

  public void sendTicketStatusUpdate(String mobileNumber, String grievanceId, String status) {
    if (!properties.getNotification().isEnabled()) {
      return;
    }
    String normalizedMobile = mobileNumber.startsWith("+") ? mobileNumber : "+91" + mobileNumber;
    Map<String, Object> body = Map.of(
        "templateId", properties.getNotification().getTemplateId(),
        "version", "v1",
        "mobileNumbers", List.of(normalizedMobile),
        "enrich", false,
        "category", "TRANSACTION",
        "payload", Map.of(
            "grievanceId", grievanceId,
            "status", status
        )
    );

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Tenant-ID", properties.getTenantId());
    headers.set("X-Client-Id", "nta-ccms");
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      restTemplate.postForEntity(resolveUrl(properties.getNotification().getSmsPath()), new HttpEntity<>(body, headers), Map.class);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "DIGIT notification failed: " + ex.getMessage());
    }
  }

  private String resolveUrl(String path) {
    String base = String.valueOf(properties.getNotification().getBaseUrl() == null ? "" : properties.getNotification().getBaseUrl()).replaceAll("/$", "");
    String suffix = String.valueOf(path == null ? "" : path);
    if (!suffix.startsWith("/")) {
      suffix = "/" + suffix;
    }
    return base + suffix;
  }
}
