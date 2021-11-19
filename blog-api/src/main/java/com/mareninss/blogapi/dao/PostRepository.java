package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.Post;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {

  @Query(value = "SELECT * FROM blog_db.posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus);

  @Query(value = "SELECT * FROM blog_db.posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus limit :limit offset :offset", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffset(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, @Param(value = "limit") int limit,
      @Param(value = "offset") int offset);

  @Query(value =
      "SELECT * FROM blog_db.posts where text LIKE CONCAT('%', :query, '%') and is_active = :isActive"
          + "  and time < :time and moderation_status = :moderationStatus limit :limit offset :offset", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, @Param(value = "limit") int limit,
      @Param(value = "offset") int offset, @Param(value = "query") String query);

}



