package com.mareninss.blogapi.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String email;

  private String password;


  private String name;

  @JsonProperty("captcha")
  private String code;

  @JsonProperty("captcha_secret")
  private String secrete;
}
