package com.mareninss.blogapi.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mareninss.blogapi.dto.ErrorDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorsResponse {

  private boolean result;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ErrorDto errors;

}
