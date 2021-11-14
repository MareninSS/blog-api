package com.mareninss.blogapi.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "captcha_codes")
@Getter
@Setter
@NoArgsConstructor
public class CaptchaCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "time", nullable = false)
  private Date time;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "secret_code", nullable = false)
  private String secretCode;

}
