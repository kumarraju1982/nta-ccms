package org.nta.ccms.master.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/masters")
public class MasterController {

  @GetMapping("/exams")
  public ResponseEntity<List<Map<String, String>>> exams() {
    return ResponseEntity.ok(List.of(
        Map.of("code", "JEE_MAIN", "name", "JEE Main"),
        Map.of("code", "NEET_UG", "name", "NEET UG"),
        Map.of("code", "CUET_UG", "name", "CUET UG")
    ));
  }

  @GetMapping("/categories")
  public ResponseEntity<List<Map<String, String>>> categories() {
    return ResponseEntity.ok(List.of(
        Map.of("code", "ADMIT_CARD", "name", "Admit Card"),
        Map.of("code", "PAYMENT", "name", "Payment"),
        Map.of("code", "APPLICATION_CORRECTION", "name", "Application Correction"),
        Map.of("code", "RESULT", "name", "Result")
    ));
  }
}
