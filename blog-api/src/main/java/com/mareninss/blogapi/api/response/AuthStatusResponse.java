package com.mareninss.blogapi.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mareninss.blogapi.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthStatusResponse {

  private boolean result;
  private UserDto user;
}
