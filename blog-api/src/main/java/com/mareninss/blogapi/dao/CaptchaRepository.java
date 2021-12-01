package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

  CaptchaCode findByCode(String code);

  @Modifying
  @Query(value =
      "DELETE FROM captcha_codes WHERE time < date_add(now(), interval - :time minute )"
          + " limit 5;", nativeQuery = true)
  void deleteByTime(@Param(value = "time") int time);
}
