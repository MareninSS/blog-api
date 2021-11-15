package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.InitResponse;
import com.mareninss.blogapi.api.response.SettingsResponse;
import com.mareninss.blogapi.service.SettingsService;
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
  private InitResponse init() {
    return initResponse;
  }

  @GetMapping("/api/settings")
  private SettingsResponse getSettings() {
    return settingsService.getGlobalSettings();
  }
}
