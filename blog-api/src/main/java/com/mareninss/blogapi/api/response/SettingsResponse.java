package com.mareninss.blogapi.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsResponse {

  @JsonProperty("MULTIUSER_MODE")
  private boolean multiUserMode = false;

  @JsonProperty("POST_PREMODERATION")
  private boolean postPreModeration = false;

  @JsonProperty("STATISTICS_IS_PUBLIC")
  private boolean statisticIsPublic = false;

}
