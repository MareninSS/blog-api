package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.AuthStatusResponse;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthStatusServiceImpl implements AuthStatusService {

  @Autowired
  private UserRepository userRepository;
  private final AuthStatusResponse authStatusResponse;

  public AuthStatusServiceImpl() {
    authStatusResponse = new AuthStatusResponse();
  }

  @Override
  public AuthStatusResponse getAuthStatus(Principal principal) {

    if (principal == null) {
      authStatusResponse.setResult(false);
      return authStatusResponse;
    }
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    authStatusResponse.setResult(true);
    authStatusResponse.setUser(DtoMapper.mapToUserDto(currentUser));
    return authStatusResponse;
  }
}

