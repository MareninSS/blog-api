package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.TagResponse;
import com.mareninss.blogapi.service.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTagController {

  @Autowired
  private TagServiceImpl tagService;

  @GetMapping("/api/tag")
  public TagResponse getTags() {
    return tagService.getTags();
  }
}
