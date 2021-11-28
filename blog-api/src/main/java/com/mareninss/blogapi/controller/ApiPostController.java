package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.CalendarCountPostResponse;
import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.service.CalendarServiceImpl;
import com.mareninss.blogapi.service.PostsServiceImpl;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  public ResponseEntity<PostsResponse> getPosts(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false, defaultValue = "recent") String mode) {
    return ResponseEntity.ok(postsService.getPosts(offset, limit, mode));
  }

  @GetMapping("/post/search")
  public ResponseEntity<PostsResponse> getPostsByQuery(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false) String query) {
    return ResponseEntity.ok(postsService.getPostsByQuery(offset, limit, query));
  }

  @GetMapping("/calendar")
  public ResponseEntity<CalendarCountPostResponse> getNumberOfPostByYears(
      @RequestParam(required = false) List<Integer> years) {
    CalendarCountPostResponse countPostResponse = calendarService.getNumberOfPostByYear(
        years);
    if (countPostResponse == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(countPostResponse);
  }

  @GetMapping("/post/byDate")
  public ResponseEntity<PostsResponse> getPostsByDates(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam String date) throws ParseException {
    return ResponseEntity.ok(postsService.getPostsByDates(offset, limit, date));
  }

  @GetMapping("/post/byTag")
  public ResponseEntity<PostsResponse> getPostsByTag(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam String tag) {
    return ResponseEntity.ok(postsService.getPostsByTag(offset, limit, tag));
  }

  @GetMapping("/post/{id}")
  public ResponseEntity<?> getPostById(@PathVariable int id, Principal principal) {
    Optional<PostByIdResponse> post = Optional.ofNullable(postsService.getPostById(id, principal));
    if (post.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found!");
    }
    return new ResponseEntity<>(post, HttpStatus.OK);
  }
}

