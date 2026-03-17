package org.nta.ccms.ticket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ccms.digit")
public class DigitIntegrationProperties {
  private String tenantId;
  private Workflow workflow = new Workflow();
  private Notification notification = new Notification();

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public Notification getNotification() {
    return notification;
  }

  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  public static class Workflow {
    private String baseUrl;
    private String processPath;
    private String transitionPath;
    private String processCode;

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getProcessPath() {
      return processPath;
    }

    public void setProcessPath(String processPath) {
      this.processPath = processPath;
    }

    public String getTransitionPath() {
      return transitionPath;
    }

    public void setTransitionPath(String transitionPath) {
      this.transitionPath = transitionPath;
    }

    public String getProcessCode() {
      return processCode;
    }

    public void setProcessCode(String processCode) {
      this.processCode = processCode;
    }
  }

  public static class Notification {
    private boolean enabled;
    private String baseUrl;
    private String smsPath;
    private String templateId;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getSmsPath() {
      return smsPath;
    }

    public void setSmsPath(String smsPath) {
      this.smsPath = smsPath;
    }

    public String getTemplateId() {
      return templateId;
    }

    public void setTemplateId(String templateId) {
      this.templateId = templateId;
    }
  }
}
