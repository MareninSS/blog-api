package com.mareninss.blogapi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDto {

  private Integer id;

  private String name;

  private String photo;

  private String email;

  @JsonProperty("moderation")
  private boolean isModerator;

  private int moderationCount;

  private boolean settings;
}
