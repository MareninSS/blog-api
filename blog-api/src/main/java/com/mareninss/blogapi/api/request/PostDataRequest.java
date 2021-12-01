package com.mareninss.blogapi.api.request;

import java.util.List;
import lombok.Data;

@Data
public class PostDataRequest {

  private long timestamp;

  private byte active;

  private String title;

  private List<String> tags;

  private String text;

}
