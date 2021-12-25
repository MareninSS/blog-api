package com.mareninss.blogapi.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDislikeRequest {

  @JsonProperty("post_id")
  Integer postId;
}
