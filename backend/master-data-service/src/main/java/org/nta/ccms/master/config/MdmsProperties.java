package org.nta.ccms.master.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ccms.digit.mdms")
public class MdmsProperties {
  private String baseUrl;
  private String searchPath;
  private String tenantId;
  private String moduleName;
  private String examsMaster;
  private String categoriesMaster;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getSearchPath() {
    return searchPath;
  }

  public void setSearchPath(String searchPath) {
    this.searchPath = searchPath;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getExamsMaster() {
    return examsMaster;
  }

  public void setExamsMaster(String examsMaster) {
    this.examsMaster = examsMaster;
  }

  public String getCategoriesMaster() {
    return categoriesMaster;
  }

  public void setCategoriesMaster(String categoriesMaster) {
    this.categoriesMaster = categoriesMaster;
  }
}
