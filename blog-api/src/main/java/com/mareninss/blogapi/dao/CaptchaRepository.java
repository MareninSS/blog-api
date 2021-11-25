package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

//  @Modifying
//  @Query("delete from CaptchaCode c where c.time < date_add()")
//  void deleteAllByTimeL(@Param("time") int time);
}
