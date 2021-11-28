package com.mareninss.blogapi.api.response;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CalendarCountPostResponse {

  private List<Integer> years;

  private Map<String, Integer> posts;
}
