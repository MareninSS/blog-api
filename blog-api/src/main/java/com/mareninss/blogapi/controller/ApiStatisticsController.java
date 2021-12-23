package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.StatisticsResponse;
import com.mareninss.blogapi.dao.SettingsRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.entity.GlobalSetting;
import com.mareninss.blogapi.entity.User;
import com.mareninss.blogapi.service.StatisticsService;
import java.security.Principal;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiStatisticsController {

  @Autowired
  private StatisticsService statisticsService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private SettingsRepository settingsRepository;

  @GetMapping("/api/statistics/my")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<StatisticsResponse> getMyStatistic(Principal principal) {
    return ResponseEntity.ok(statisticsService.getStatistics(principal));
  }

  @GetMapping("/api/statistics/all")
  public ResponseEntity<StatisticsResponse> getAllStatistics(Principal principal) {
    final String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";
    boolean isModerator = false;
    if (principal != null) {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      isModerator = currentUser.getIsModerator() == 1;
    }
    boolean isStatisticsPublic = settingsRepository.getGlobalSettingsByCodeIs(STATISTICS_IS_PUBLIC)
        .getValue().equals("yes");

    if (isModerator || isStatisticsPublic) {
      return ResponseEntity.ok(statisticsService.getAllStatistics(principal));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
