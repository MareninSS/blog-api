package com.mareninss.blogapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsDto {

  private Integer id;
  private long timestamp;
  private String text;
  private UserCommentsDto user;
}
