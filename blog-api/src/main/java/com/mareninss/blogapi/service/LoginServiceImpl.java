package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.LoginRequest;
import com.mareninss.blogapi.api.response.LoginResponse;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private UserRepository userRepository;
  private final AuthenticationManager authenticationManager;

  private final LoginResponse loginResponse;

  public LoginServiceImpl(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
    loginResponse = new LoginResponse();
  }


  @Override
  public LoginResponse checkAndAuth(LoginRequest loginRequest) {
    Authentication auth = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
            loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    User user = (User) auth.getPrincipal();
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(user.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException(
            user.getUsername()));

    loginResponse.setResult(true);
    loginResponse.setUser(DtoMapper.mapToUserDto(currentUser));
    return loginResponse;
  }
}
