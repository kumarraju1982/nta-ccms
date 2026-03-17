package org.nta.ccms.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.nta.ccms.master.config.MdmsProperties;

@SpringBootApplication
@EnableConfigurationProperties(MdmsProperties.class)
public class MasterDataServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataServiceApplication.class, args);
  }
}
