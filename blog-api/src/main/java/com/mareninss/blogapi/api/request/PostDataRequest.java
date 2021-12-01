package com.mareninss.blogapi.api.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDataRequest {

  private long timestamp;

  private byte active;

  private String title;

  private List<String> tags;

  private String text;

}
