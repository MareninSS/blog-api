package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.api.request.LoginRequest;
import com.mareninss.blogapi.api.response.LoginResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  private AuthenticationManager authenticationManager;

  public LoginServiceImpl(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }


  @Override
  public LoginResponse checkAndAuth(LoginRequest loginRequest) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      Authentication auth = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
              loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(auth);
    User user = (User) auth.getPrincipal();

    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(user.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException(
            user.getUsername()));
    int moderationCount = postRepository.getAllByModerationStatusAndModeratorIdIs(
        ModerationStatus.NEW, null).size();
    loginResponse.setResult(true);
    loginResponse.setUser(DtoMapper.mapToUserDto(currentUser, moderationCount));
    } catch (BadCredentialsException e) {
      System.out.println("BadCredentialsException  - Bad Credentials!");
    }
    return loginResponse;
  }
}
