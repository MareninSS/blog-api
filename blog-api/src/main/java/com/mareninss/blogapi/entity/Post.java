package com.mareninss.blogapi.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "is_active", nullable = false)
  private Byte isActive;

  @Column(name = "moderation_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ModerationStatusEnum moderationStatus;

  @Column(name = "moderator_id")
  private Integer moderatorId;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "time", nullable = false)
  private Date time;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "text", nullable = false)
  private String text;

  @Column(name = "view_count", nullable = false)
  private Integer viewCount;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "post_id")
  private List<PostVote> postVotes;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
      CascadeType.DETACH})
  @JoinTable(name = "tag2post"
      , joinColumns = @JoinColumn(name = "post_id")
      , inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<Tag> tags;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "post_id")
  private List<PostComment> postComments;

}
