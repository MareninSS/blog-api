package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.AuthStatusResponse;
import java.security.Principal;

public interface AuthStatusService {

  AuthStatusResponse getAuthStatus(Principal principal);

}
