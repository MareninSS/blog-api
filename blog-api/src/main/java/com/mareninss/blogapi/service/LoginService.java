package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.LoginRequest;
import com.mareninss.blogapi.api.response.LoginResponse;

public interface LoginService {

  LoginResponse checkAndAuth(LoginRequest loginRequest);

}
