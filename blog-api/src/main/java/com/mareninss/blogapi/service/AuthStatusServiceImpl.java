package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.AuthStatusResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.entity.ModerationStatus;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthStatusServiceImpl implements AuthStatusService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;

  @Override
  public AuthStatusResponse getAuthStatus(Principal principal) {
    AuthStatusResponse authStatusResponse = new AuthStatusResponse();
    if (principal == null) {
      authStatusResponse.setResult(false);
      return authStatusResponse;
    }
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    int moderationCount = postRepository.getAllByModerationStatusAndModeratorIdIs(
        ModerationStatus.NEW, null).size();
    authStatusResponse.setResult(true);
    authStatusResponse.setUser(DtoMapper.mapToUserDto(currentUser, moderationCount));
    return authStatusResponse;
  }
}

