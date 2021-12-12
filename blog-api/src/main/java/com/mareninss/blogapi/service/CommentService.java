package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.CommentRequest;
import java.security.Principal;

public interface CommentService {

  <T> Object addComment(CommentRequest commentRequest, Principal principal);
}
