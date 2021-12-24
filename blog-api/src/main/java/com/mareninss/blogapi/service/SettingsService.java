package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.SettingsRequest;
import com.mareninss.blogapi.api.response.SettingsResponse;
import com.mareninss.blogapi.dao.SettingsRepository;
import com.mareninss.blogapi.entity.GlobalSetting;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {


  @Autowired
  private SettingsRepository settingsRepository;
  private final SettingsResponse settingsResponse;

  public SettingsService() {
    settingsResponse = new SettingsResponse();
  }

  public SettingsResponse getGlobalSettings() {
    List<GlobalSetting> globalSetting = settingsRepository.getAllBy();
    try {
      settingsResponse.setMultiUserMode(setSettingsValue(globalSetting, 1));
      settingsResponse.setPostPreModeration(setSettingsValue(globalSetting, 2));
      settingsResponse.setStatisticIsPublic(setSettingsValue(globalSetting, 3));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return settingsResponse;
  }

  private boolean setSettingsValue(List<GlobalSetting> globalSettings, int idMode)
      throws SQLException {
    var numberModes = 2;
    if (globalSettings.isEmpty() || globalSettings.size() < numberModes) {
      throw new SQLException("There should be three modes for global settings");
    }
    return Objects.equals(globalSettings.get(idMode - 1).getValue().toUpperCase(Locale.ROOT),
        "YES");
  }

  public void saveSettings(SettingsRequest request) {
    if (request != null) {
      List<GlobalSetting> globalSetting = settingsRepository.getAllBy();
      int MultiUserMode = 0;
      int PostPreModerationMode = 1;
      int StatisticIsPublicMode = 2;
      globalSetting.get(MultiUserMode).setValue(request.isMultiUserMode() ? "yes" : "no");
      globalSetting.get(PostPreModerationMode).setValue(request.isPostPreModeration() ? "yes" : "no");
      globalSetting.get(StatisticIsPublicMode).setValue(request.isStatisticIsPublic() ? "yes" : "no");
      settingsRepository.saveAllAndFlush(globalSetting);
    }
  }

}
