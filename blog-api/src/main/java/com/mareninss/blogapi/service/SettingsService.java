package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {


  public SettingsResponse getGlobalSettings() {
    SettingsResponse settingsResponse = new SettingsResponse();
    settingsResponse.setMultiUserMode(true);
    settingsResponse.setPostPreModeration(true);
    settingsResponse.setStatisticIsPublic(true);

    return settingsResponse;
  }

}
