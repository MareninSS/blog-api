package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.StatisticsResponse;
import com.mareninss.blogapi.entity.User;
import com.mareninss.blogapi.service.StatisticsService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiStatisticsController {

  @Autowired
  private StatisticsService statisticsService;

  @GetMapping("/api/statistics/my")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<StatisticsResponse> getStatistic(Principal principal) {
    return ResponseEntity.ok(statisticsService.getStatistics(principal));
  }
}
