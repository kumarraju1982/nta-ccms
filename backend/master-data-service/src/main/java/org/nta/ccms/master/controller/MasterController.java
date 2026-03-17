package org.nta.ccms.master.controller;

import java.util.List;
import java.util.Map;
import org.nta.ccms.master.service.MdmsClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/masters")
public class MasterController {
  private final MdmsClient mdmsClient;

  public MasterController(MdmsClient mdmsClient) {
    this.mdmsClient = mdmsClient;
  }

  @GetMapping("/exams")
  public ResponseEntity<List<Map<String, String>>> exams() {
    return ResponseEntity.ok(mdmsClient.fetchExams());
  }

  @GetMapping("/categories")
  public ResponseEntity<List<Map<String, String>>> categories() {
    return ResponseEntity.ok(mdmsClient.fetchCategories());
  }
}
