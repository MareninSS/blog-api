package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.CalendarCountPostResponse;
import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.service.CalendarServiceImpl;
import com.mareninss.blogapi.service.PostsServiceImpl;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {

  @Autowired
  private PostsServiceImpl postsService;

  @Autowired
  private CalendarServiceImpl calendarService;

  @GetMapping("/post")
  public PostsResponse getPosts(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String mode) {
    return postsService.getPosts(offset, limit, mode);
  }

  @GetMapping("/post/search")
  public PostsResponse getPostsByQuery(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String query) {
    return postsService.getPostsByQuery(offset, limit, query);
// TODO: 19.11.2021 запрос пустой ?
  }

  @GetMapping("/calendar")
  public CalendarCountPostResponse getNumberOfPostByYears(
      @RequestParam(required = false) List<Integer> years) {
    return calendarService.getNumberOfPostByYear(years);
  }

  @GetMapping("/post/byDate")
  public PostsResponse getPostsByDates(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String date) throws ParseException {
    return postsService.getPostsByDates(offset, limit, date);
  }

  @GetMapping("/post/byTag")
  public PostsResponse getPostsByTag(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String tag) {
    return postsService.getPostsByTag(offset, limit, tag);
  }

  @GetMapping("/post/{id}")
  public ResponseEntity<?> getPostById(@PathVariable int id) {
    Optional<PostByIdResponse> post = Optional.ofNullable(postsService.getPostById(id));
    if (post.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found!");
    }
    return new ResponseEntity<>(post, HttpStatus.OK);
  }
}

