package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.InitResponse;
import com.mareninss.blogapi.api.response.SettingsResponse;
import com.mareninss.blogapi.service.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {


  private final SettingsService settingsService;
  private final InitResponse initResponse;

  public ApiGeneralController(SettingsService settingsService,
      InitResponse initResponse) {
    this.settingsService = settingsService;
    this.initResponse = initResponse;
  }

  @RequestMapping("/api/init")
  public InitResponse init() {
    return initResponse;
  }

  @GetMapping("/api/settings")
  public SettingsResponse getSettings() {
    return settingsService.getGlobalSettings();
  }
}
