package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostsResponse;



public interface PostsService {

  PostsResponse getPosts(int offset, int limit, String mode);

  PostsResponse getPostsByQuery(int offset, int limit, String query);
}
