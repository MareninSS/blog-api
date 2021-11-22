package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.AuthStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthStatusService {

  public AuthStatusResponse getAuthStatus() {
    AuthStatusResponse authStatusResponse = new AuthStatusResponse();
    authStatusResponse.setResult(false);
    return authStatusResponse;
  }
}

