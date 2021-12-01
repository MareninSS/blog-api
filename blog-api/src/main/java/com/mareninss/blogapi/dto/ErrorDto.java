package com.mareninss.blogapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

  private String email;

  private String name;

  private String password;

  private String captcha;

  private String title;

  private String text;

}
