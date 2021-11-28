package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {

  @Query(value = "SELECT * FROM blog_db.posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus);

  @Query(value = "SELECT * FROM blog_db.posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffset(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);

  @Query(value =
      "SELECT * FROM blog_db.posts where text LIKE CONCAT('%', :query, '%') and is_active = :isActive"
          + "  and time < :time and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param(value = "query") String query, Pageable pageable);

  @Query(value = "SELECT * FROM blog_db.posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus and YEAR(time) = :years", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_AcceptedWhereYearsEqual(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param("years") List<Integer> years);

  @Query(value = "SELECT * FROM blog_db.posts "
      + "where is_active = :isActive "
      + "and time < :time and time like concat(:timeNow,'%') "
      + "and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "timeNow") String timeNow,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);

  @Query(value = "SELECT posts.*\n"
      + "FROM blog_db.posts\n"
      + "join blog_db.tag2post on posts.id = tag2post.post_id\n"
      + "join blog_db.tags on tags.id = tag2post.tag_id\n"
      + "where posts.is_active = :isActive "
      + "and posts.time < :time "
      + "and posts.moderation_status = :moderationStatus "
      + "and tags.name like concat(:tag,'%')", nativeQuery = true)
  List<Post> findPostsByTagNameAndIsActiveAndTimeIsLessThanAndModerationStatus(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, @Param("tag") String tag,
      Pageable pageable);

  Optional<Post> findPostByIdAndIsActiveAndTimeIsLessThanAndModerationStatus(Integer id,
      Byte isActive, Date time, ModerationStatus moderationStatus);

  List<Post> getAllByModerationStatusAndModeratorIdIs(ModerationStatus moderationStatus,
      Integer moderatorId);

  @Modifying
  @Query(value = "UPDATE blog_db.posts SET view_count = view_count + 1 WHERE id = :id", nativeQuery = true)
  int updateViewCountById(@Param(value = "id") int id);
}



