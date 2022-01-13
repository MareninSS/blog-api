package com.mareninss.blogapi.api.response;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CalendarCountPostResponse {

  private Set<Integer> years;

  private Map<String, Integer> posts;
}
