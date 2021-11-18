package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.AuthStatusResponse;
import com.mareninss.blogapi.service.AuthStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {

  private final AuthStatusService authStatusService;

  public ApiAuthController(AuthStatusService authStatusService) {
    this.authStatusService = authStatusService;
  }

  @GetMapping("/api/auth/check")
  public AuthStatusResponse getAuthStatus() {
    return authStatusService.getAuthStatus();
  }

}
