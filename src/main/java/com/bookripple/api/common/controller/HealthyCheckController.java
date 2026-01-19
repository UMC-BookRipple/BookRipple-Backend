package com.bookripple.api.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyCheckController {

  @GetMapping("/")
  public ResponseEntity<String> check() {
    return ResponseEntity.ok("ok");
  }
}
