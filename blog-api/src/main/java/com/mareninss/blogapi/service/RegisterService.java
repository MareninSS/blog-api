package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.RegisterResponse;
import org.springframework.validation.BindingResult;

public interface RegisterService {

  RegisterResponse createUser(RegisterRequest registerRequest);
}
