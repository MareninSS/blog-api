package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.api.request.CommentRequest;
import com.mareninss.blogapi.dao.CommentRepository;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.PostComment;
import com.mareninss.blogapi.service.CommentService;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private UserRepository userRepository;

  @Override
  public Map<String, Integer> addComment(CommentRequest commentRequest, Principal principal) {
    if (principal != null) {
      Map<String, Integer> result = new HashMap<>();
      Optional<PostComment> comment = commentRepository.findById(commentRequest.getParentId());
      Optional<Post> post = postRepository.findById(commentRequest.getPostId());
      com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
              principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      PostComment postComment = new PostComment();

      if (comment.isEmpty() && post.isPresent()
          && post.get().getId() == commentRequest.getPostId()) {
        postComment.setPostId(commentRequest.getPostId());
        postComment.setParentId(null);
        postComment.setUserId(currentUser.getId());
        postComment.setTime(new Date());
        postComment.setText(commentRequest.getText());
        commentRepository.saveAndFlush(postComment);
        result.put("id", postComment.getId());
      } else if (comment.isPresent() && comment.get().getId() == commentRequest.getParentId()
          && post.isPresent() && post.get().getId() == commentRequest.getPostId()) {
        postComment.setPostId(commentRequest.getPostId());
        postComment.setParentId(commentRequest.getParentId());
        postComment.setUserId(currentUser.getId());
        postComment.setTime(new Date());
        postComment.setText(commentRequest.getText());
        commentRepository.saveAndFlush(postComment);
        result.put("id", postComment.getId());
      }
      return result;
    }
    return null;
  }
}
