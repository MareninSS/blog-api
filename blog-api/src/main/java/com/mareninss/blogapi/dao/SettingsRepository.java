package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.GlobalSetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<GlobalSetting, Integer> {

  List<GlobalSetting> getAllBy();

  GlobalSetting getGlobalSettingsByCodeIs(String code);
}
