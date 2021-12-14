package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.request.CommentRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.dao.CommentRepository;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.PostComment;
import com.mareninss.blogapi.service.CommentService;
import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiCommentController {

  @Autowired
  private CommentService commentService;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private CommentRepository commentRepository;
  private final ErrorsResponse errorsResponse;

  public ApiCommentController() {
    errorsResponse = new ErrorsResponse();
  }

  @PostMapping("/api/comment")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest,
      Principal principal) {
    Optional<PostComment> comment = commentRepository.findById(commentRequest.getParentId());
    Optional<Post> post = postRepository.findById(commentRequest.getPostId());

    ErrorDto errorDto = new ErrorDto();
    if (commentRequest.getText().length() < 5 || commentRequest.getText().isEmpty()) {
      errorDto.setText("Текст комментария не задан или слишком короткий");
    }
    if (comment.isEmpty() && post.isEmpty()) {
      errorDto.setText("Поста и комментария не существует");
    }
    if (hasErrors(errorDto)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(errorDto);
      return ResponseEntity.badRequest().body(errorsResponse);
    } else {
      return ResponseEntity.ok(commentService.addComment(commentRequest, principal));
    }
  }

  private boolean hasErrors(ErrorDto errors) {
    return errors.getName() != null || errors.getPassword() != null || errors.getEmail() != null
        || errors.getCaptcha() != null || errors.getText() != null || errors.getTitle() != null;
  }
}
