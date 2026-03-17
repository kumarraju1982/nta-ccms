package org.nta.ccms.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ccms.digit.auth")
public class DigitAuthProperties {
  private String baseUrl;
  private String tokenPath;
  private String userInfoPath;
  private String clientId;
  private String clientSecret;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getTokenPath() {
    return tokenPath;
  }

  public void setTokenPath(String tokenPath) {
    this.tokenPath = tokenPath;
  }

  public String getUserInfoPath() {
    return userInfoPath;
  }

  public void setUserInfoPath(String userInfoPath) {
    this.userInfoPath = userInfoPath;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }
}
