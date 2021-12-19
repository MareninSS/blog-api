package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.request.EditProfileRequest;
import com.mareninss.blogapi.api.request.LoginRequest;
import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.AuthStatusResponse;
import com.mareninss.blogapi.api.response.CaptchaResponse;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.api.response.LoginResponse;
import com.mareninss.blogapi.service.AuthStatusServiceImpl;
import com.mareninss.blogapi.service.CaptchaService;
import com.mareninss.blogapi.service.LoginService;
import com.mareninss.blogapi.service.RegisterService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiAuthController {

  @Autowired
  private AuthStatusServiceImpl authStatusService;
  @Autowired
  private CaptchaService captchaService;
  @Autowired
  private LoginService loginService;
  @Autowired
  private RegisterService registerService;

  @GetMapping("/api/auth/check")
  public ResponseEntity<AuthStatusResponse> getAuthStatus(Principal principal) {
    return ResponseEntity.ok(authStatusService.getAuthStatus(principal));
  }

  @GetMapping("/api/auth/captcha")
  public ResponseEntity<CaptchaResponse> getCaptcha() {
    CaptchaResponse captcha = captchaService.generateCaptcha();
    if (captcha == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(captchaService.generateCaptcha());
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return new ResponseEntity<>(loginService.checkAndAuth(loginRequest), HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  public ResponseEntity<ErrorsResponse> register(@RequestBody RegisterRequest registerRequest) {
    return new ResponseEntity<>(registerService.createUser(registerRequest), HttpStatus.OK);
  }

  @GetMapping("/api/auth/logout")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public Map<String, Boolean> logout() {
    Map<String, Boolean> result = new HashMap<>();
    result.put("result", true);
    return result;
  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<ErrorsResponse> editProfile(@RequestBody EditProfileRequest request,
      Principal principal) {
    return ResponseEntity.ok(registerService.editProfileJSON(request, principal));
  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<ErrorsResponse> editProfile(
      @RequestParam(name = "photo") MultipartFile photo,
      @RequestParam String name,
      @RequestParam String email,
      @RequestParam(required = false) String password,
      @RequestParam Integer removePhoto,
      Principal principal) {
    return ResponseEntity.ok(
        registerService.editProfileMFD(photo, name, email, password, removePhoto, principal));
  }
}
