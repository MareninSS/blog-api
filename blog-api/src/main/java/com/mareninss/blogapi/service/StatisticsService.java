package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.StatisticsResponse;
import com.mareninss.blogapi.entity.User;
import java.security.Principal;

public interface StatisticsService {

  StatisticsResponse getStatistics(Principal principal);
}
