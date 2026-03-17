package org.nta.ccms.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.nta.ccms.ticket.config.DigitIntegrationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DigitIntegrationProperties.class)
public class TicketServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TicketServiceApplication.class, args);
  }
}
