package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.EditProfileRequest;
import com.mareninss.blogapi.api.request.RecoverRequest;
import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface RegisterService {

  ErrorsResponse createUser(RegisterRequest registerRequest);

  ErrorsResponse editProfileJSON(EditProfileRequest request, Principal principal);

  ErrorsResponse editProfileMFD(MultipartFile photo, String name, String email, String password,
      Integer removePhoto, Principal principal);

  Map<String, Boolean> recoverPass(RecoverRequest email);
}
