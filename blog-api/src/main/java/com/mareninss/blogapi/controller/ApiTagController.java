package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.TagResponse;
import com.mareninss.blogapi.service.service_impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTagController {

  @Autowired
  private TagServiceImpl tagService;

  @GetMapping("/api/tag")
  public ResponseEntity<TagResponse> getTags(@RequestParam(required = false) String query) {
    return ResponseEntity.ok(tagService.getTags(query));
  }
}
