package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.request.LoginRequest;
import com.mareninss.blogapi.api.response.AuthStatusResponse;
import com.mareninss.blogapi.api.response.CaptchaResponse;
import com.mareninss.blogapi.api.response.LoginResponse;
import com.mareninss.blogapi.service.AuthStatusServiceImpl;
import com.mareninss.blogapi.service.CaptchaService;
import com.mareninss.blogapi.service.LoginService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {


  @Autowired
  private AuthStatusServiceImpl authStatusService;
  @Autowired
  private CaptchaService captchaService;
  @Autowired
  private LoginService loginService;

  @GetMapping("/api/auth/check")
  public AuthStatusResponse getAuthStatus(Principal principal) {
    return authStatusService.getAuthStatus(principal);
  }

  @GetMapping("/api/auth/captcha")
  public CaptchaResponse getCaptcha() {
    return captchaService.generateCaptcha();
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(loginService.checkAndAuth(loginRequest));
  }
}
