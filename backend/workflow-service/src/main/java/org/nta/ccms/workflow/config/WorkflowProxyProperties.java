package org.nta.ccms.workflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ccms.digit.workflow")
public class WorkflowProxyProperties {
  private String baseUrl;
  private String transitionPath;
  private String tenantId;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getTransitionPath() {
    return transitionPath;
  }

  public void setTransitionPath(String transitionPath) {
    this.transitionPath = transitionPath;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
