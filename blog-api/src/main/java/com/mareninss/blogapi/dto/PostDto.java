package com.mareninss.blogapi.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {

  private Integer id;

  private long timestamp;

  private UserPostDto user;

  private String title;

  private String announce;

  private Integer likeCount;

  private Integer dislikeCount;

  private Integer commentCount;

  private Integer viewCount;

}
