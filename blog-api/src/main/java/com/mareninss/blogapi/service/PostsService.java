package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostsResponse;
import org.springframework.stereotype.Service;


public interface PostsService {

  public PostsResponse getPosts(int offset, int limit, String mode);
}
