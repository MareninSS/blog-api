package com.mareninss.blogapi.api.response;

import com.mareninss.blogapi.dto.TagDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponse {

  private List<TagDto> tags;
}
