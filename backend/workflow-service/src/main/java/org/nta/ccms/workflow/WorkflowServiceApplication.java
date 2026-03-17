package org.nta.ccms.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.nta.ccms.workflow.config.WorkflowProxyProperties;

@SpringBootApplication
@EnableConfigurationProperties(WorkflowProxyProperties.class)
public class WorkflowServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(WorkflowServiceApplication.class, args);
  }
}
