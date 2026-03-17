package org.nta.ccms.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.nta.ccms.auth.config.DigitAuthProperties;

@SpringBootApplication
@EnableConfigurationProperties(DigitAuthProperties.class)
public class AuthAccessServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuthAccessServiceApplication.class, args);
  }
}
