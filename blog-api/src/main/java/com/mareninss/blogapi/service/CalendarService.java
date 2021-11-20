package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.CalendarCountPostResponse;
import java.util.List;

public interface CalendarService {

  CalendarCountPostResponse getNumberOfPostByYear(List<Integer> years);
}
