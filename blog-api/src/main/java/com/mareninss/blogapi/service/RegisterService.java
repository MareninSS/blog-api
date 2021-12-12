package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;

public interface RegisterService {

  ErrorsResponse createUser(RegisterRequest registerRequest);
}
