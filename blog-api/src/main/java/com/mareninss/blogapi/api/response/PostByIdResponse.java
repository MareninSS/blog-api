package com.mareninss.blogapi.api.response;

import com.mareninss.blogapi.dto.CommentsDto;
import com.mareninss.blogapi.dto.UserPostDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostByIdResponse {

  private Integer id;
  private long timestamp;
  private boolean active;
  private UserPostDto user;
  private String title;
  private String text;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer viewCount;
  private List<CommentsDto> comments;
  private List<String> tags;


}
