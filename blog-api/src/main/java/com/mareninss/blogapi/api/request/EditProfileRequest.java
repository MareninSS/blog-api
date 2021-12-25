package com.mareninss.blogapi.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {

  @Nullable
  private String photo;

  private String name;

  private String email;

  private String password;

  private Integer removePhoto;

}
