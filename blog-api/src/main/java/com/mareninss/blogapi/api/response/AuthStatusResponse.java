package com.mareninss.blogapi.api.response;


import com.mareninss.blogapi.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthStatusResponse {

  private boolean result = false;
  private UserDto userDto;
}
