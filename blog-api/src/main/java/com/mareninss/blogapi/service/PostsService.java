package com.mareninss.blogapi.service;


import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import java.security.Principal;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface PostsService {

  PostsResponse getPosts(int offset, int limit, String mode);

  PostsResponse getPostsByQuery(int offset, int limit, String query);

  PostsResponse getPostsByDates(int offset, int limit, String date) throws ParseException;

  PostsResponse getPostsByTag(int offset, int limit, String tag);

  PostByIdResponse getPostById(int id, Principal principal);
}