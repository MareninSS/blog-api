package com.mareninss.blogapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {

  private Integer id;

  private long timestamp;

  @JsonProperty("user")
  private UserPostDto userPostDto;

  private String title;

  private String announce;

  private Integer likeCount;

  private Integer dislikeCount;

  private Integer commentCount;

  private Integer viewCount;


}
