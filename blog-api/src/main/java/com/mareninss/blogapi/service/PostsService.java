package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostsResponse;
import java.text.ParseException;


public interface PostsService {

  PostsResponse getPosts(int offset, int limit, String mode);

  PostsResponse getPostsByQuery(int offset, int limit, String query);

  PostsResponse getPostsByDates(int offset, int limit, String date) throws ParseException;
}
