package com.mareninss.blogapi.api.response;

import com.mareninss.blogapi.dto.PostDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostsResponse {

  private Integer count;
  private List<PostDto> posts;
}
