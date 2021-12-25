package com.mareninss.blogapi.entity;

import com.mareninss.blogapi.config.Role;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "is_moderator", nullable = false)
  private Integer isModerator;

  @Column(name = "reg_time", nullable = false)
  private Date regTime;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "code")
  private String code;

  @Column(name = "time_code")
  private Date timeCode;

  @Column(name = "photo")
  private String photo;

  public Role getRole() {
    return isModerator == 1 ? Role.MODERATOR : Role.USER;
  }
}
