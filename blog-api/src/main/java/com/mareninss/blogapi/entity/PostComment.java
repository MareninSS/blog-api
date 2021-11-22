package com.mareninss.blogapi.entity;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@NoArgsConstructor
public class PostComment {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "parent_id")
  private Integer parentId;

  @Column(name = "post_id", nullable = false)
  private Integer postId;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "time", nullable = false)
  private Date time;

  @Column(name = "text", nullable = false)
  private String text;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
      CascadeType.DETACH})
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private PostComment postComment;

}
