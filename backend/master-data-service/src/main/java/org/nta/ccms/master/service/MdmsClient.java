package org.nta.ccms.master.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.nta.ccms.master.config.MdmsProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class MdmsClient {
  private final RestTemplate restTemplate = new RestTemplate();
  private final MdmsProperties properties;

  public MdmsClient(MdmsProperties properties) {
    this.properties = properties;
  }

  public List<Map<String, String>> fetchExams() {
    return fetchMasterAsCodeName(properties.getExamsMaster());
  }

  public List<Map<String, String>> fetchCategories() {
    return fetchMasterAsCodeName(properties.getCategoriesMaster());
  }

  private List<Map<String, String>> fetchMasterAsCodeName(String masterName) {
    Map<String, Object> response = mdmsSearch(masterName);
    Object mdmsRes = response.get("MdmsRes");
    if (!(mdmsRes instanceof Map<?, ?> mdmsResMap)) {
      return List.of();
    }
    Object moduleObj = mdmsResMap.get(properties.getModuleName());
    if (!(moduleObj instanceof Map<?, ?> moduleMap)) {
      return List.of();
    }
    Object masterObj = moduleMap.get(masterName);
    if (!(masterObj instanceof List<?> rows)) {
      return List.of();
    }

    List<Map<String, String>> out = new ArrayList<>();
    for (Object rowObj : rows) {
      if (rowObj instanceof Map<?, ?> row) {
        String code = stringOr(row.get("code"), stringOr(row.get("id"), stringOr(row.get("name"), "")));
        String name = stringOr(row.get("name"), code);
        if (!code.isBlank()) {
          out.add(Map.of("code", code, "name", name));
        }
      } else if (rowObj instanceof String s && !s.isBlank()) {
        out.add(Map.of("code", s, "name", s));
      }
    }
    return out;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> mdmsSearch(String masterName) {
    Map<String, Object> payload = Map.of(
        "MdmsCriteria", Map.of(
            "tenantId", properties.getTenantId(),
            "moduleDetails", List.of(
                Map.of(
                    "moduleName", properties.getModuleName(),
                    "masterDetails", List.of(Map.of("name", masterName))
                )
            )
        )
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Tenant-ID", properties.getTenantId());
    headers.set("X-Client-Id", "nta-ccms");
    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(resolveUrl(properties.getSearchPath()), new HttpEntity<>(payload, headers), Map.class);
      return response.getBody() == null ? Map.of() : response.getBody();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "MDMS search failed: " + ex.getMessage());
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

  private String stringOr(Object value, String fallback) {
    return value == null ? fallback : String.valueOf(value);
  }
}
