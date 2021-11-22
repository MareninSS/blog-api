package com.mareninss.blogapi.api.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class InitResponse {

  @Value("${blog.title}")
  private String title;

  @Value("${blog.subtitle}")
  private String subtitle;

  @Value("${blog.phone}")
  private String phone;

  private String email;

  private String copyright;

  private String copyrightFrom;

}
