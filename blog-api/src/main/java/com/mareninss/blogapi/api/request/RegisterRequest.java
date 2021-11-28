package com.mareninss.blogapi.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @JsonProperty("e_mail")
  @Email(regexp = "^[-a-z0-9!#$%&'*+/=?^_`{|}~]+"
      + "(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*"
      + "@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)"
      + "*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info"
      + "|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$")
  @NotNull(message = "Email is a required field")
  private String email;

  @NotNull(message = "Password is a required field")
  @Size(min = 6, max = 16, message = "Password must be equal to or greater than 6 characters "
      + "and less than 16 characters")
  private String password;

  @NotNull(message = "Name cannot be missing or empty")
  @Size(min = 2, message = "First name must not be less than 2 characters")
  private String name;

  @JsonProperty("captcha")
  private String code;

  @JsonProperty("captcha_secret")
  private String secrete;
}
