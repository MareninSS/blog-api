package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.service.PostsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

  @Autowired
  private PostsServiceImpl postsService;

  @GetMapping("/api/post")
  public PostsResponse getPosts(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String mode) {
    return postsService.getPosts(offset, limit, mode);
  }

  @GetMapping("/api/post/search")
  public PostsResponse getPostsByQuery(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String query) {
    return postsService.getPostsByQuery(offset, limit, query);
// TODO: 19.11.2021 запрос пустой ?
  }
}
