package com.mareninss.blogapi.dao;


import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

public interface PostRepository extends JpaRepository<Post, Integer> {

  @Query(value = "SELECT * FROM posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus);

  @Query(value =
      "SELECT * FROM posts where text LIKE CONCAT('%', :query, '%') and is_active = :isActive"
          + "  and time < :time and moderation_status = :moderationStatus", countQuery =
      "SELECT count(*) FROM posts where posts.is_active = 1 "
          + "and posts.time < date(now()) "
          + "and posts.moderation_status = 'ACCEPTED'", nativeQuery = true)
  Page<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param(value = "query") String query, Pageable pageable);


  @Query(value = "SELECT * FROM posts where is_active = :isActive"
      + "  and time < :time and moderation_status = :moderationStatus and YEAR(time) = :years", nativeQuery = true)
  List<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_AcceptedWhereYearsEqual(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param("years") Set<Integer> years);


  @Query(value = "SELECT * FROM posts "
      + "where is_active = :isActive "
      + "and time < :time and time like concat(:timeNow,'%') "
      + "and moderation_status = :moderationStatus", countQuery = "SELECT count(*) FROM posts "
      + "where is_active = :isActive "
      + "and time < :time and time like concat(:timeNow,'%') "
      + "and moderation_status = :moderationStatus", nativeQuery = true)
  Page<Post> getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "timeNow") String timeNow,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);

  @Query(value = "SELECT posts.*\n"
      + "FROM posts\n"
      + "join tag2post on posts.id = tag2post.post_id\n"
      + "join tags on tags.id = tag2post.tag_id\n"
      + "where posts.is_active = :isActive "
      + "and posts.time < :time "
      + "and posts.moderation_status = :moderationStatus "
      + "and tags.name like concat(:tag,'%')", countQuery = "SELECT count(*) "
      + "FROM posts "
      + "join tag2post on posts.id = tag2post.post_id "
      + "join tags on tags.id = tag2post.tag_id "
      + "where posts.is_active = :isActive "
      + "and posts.time < :time "
      + "and posts.moderation_status = :moderationStatus "
      + "and tags.name like concat(:tag,'%')", nativeQuery = true)
  Page<Post> findPostsByTagNameAndIsActiveAndTimeIsLessThanAndModerationStatus(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, @Param("tag") String tag,
      Pageable pageable);

  Optional<Post> findPostByIdAndIsActiveAndTimeIsLessThanAndModerationStatus(Integer id,
      Byte isActive, Date time, ModerationStatus moderationStatus);

  List<Post> getAllByModerationStatusAndModeratorIdIs(ModerationStatus moderationStatus,
      Integer moderatorId);

  @Modifying
  @Query(value = "UPDATE posts SET view_count = view_count + 1 WHERE id = :id", nativeQuery = true)
  int updateViewCountById(@Param(value = "id") int id);

  @Query(value =
      "select * from posts where is_active = :isActive and moderation_status = :moderationStatus"
          + " and moderator_id = :moderatorId", countQuery =
      "select count(*) from posts where is_active = :isActive and moderation_status = :moderationStatus "
          + "and moderator_id = :moderatorId", nativeQuery = true)
  Page<Post> getAllByIsActiveAndModerationStatusAndModeratorId(
      @Param(value = "isActive") Byte isActive,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param(value = "moderatorId") @Nullable Integer moderatorId, Pageable pageable);

  @Query(value =
      "select * from posts where is_active = :isActive and moderation_status = :moderationStatus"
          + " and moderator_id is null", countQuery =
      "select count(*) from posts where is_active = :isActive and moderation_status = :moderationStatus "
          + "and moderator_id is null", nativeQuery = true)
  Page<Post> getAllByIsActiveAndModerationStatusAndModeratorId(
      @Param(value = "isActive") Byte isActive,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);

  @Query(value =
      "select * from posts where is_active = :isActive and moderation_status = :moderationStatus "
          + "and user_id = :authorId", countQuery =
      "select * from posts where is_active = :isActive and moderation_status = :moderationStatus "
          + "and user_id = :authorId", nativeQuery = true)
  Page<Post> getAllByIsActiveAndModerationStatusAndAuthorIdIs(
      @Param(value = "isActive") Byte isActive,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param(value = "authorId") Integer authorId, Pageable pageable);

  @Query(value =
      "select * from posts where is_active = :isActive and moderation_status = :moderationStatus "
          + "and user_id = :authorId", nativeQuery = true)
  List<Post> getAllByIsActiveAndModerationStatusAndAuthorIdIs(
      @Param(value = "isActive") Byte isActive,
      @Param(value = "moderationStatus") String moderationStatus,
      @Param(value = "authorId") Integer authorId);

  List<Post> getAllByIsActiveAndModerationStatus(Byte isActive, ModerationStatus moderationStatus);


  Page<Post> getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByTimeDesc(Byte isActive,
      ModerationStatus moderationStatus, Date time, Pageable pageable);

  Page<Post> getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByTimeAsc(Byte isActive,
      ModerationStatus moderationStatus, Date time, Pageable pageable);

  @Query(value = "select posts.* "
      + "from posts "
      + "left join "
      + "(select post_id, count(post_id) as comment_count from post_comments group by post_id) "
      + "as comments on posts.id = comments.post_id "
      + "where posts.is_active = :isActive "
      + "and posts.time < :time "
      + "and posts.moderation_status = :moderationStatus "
      + "order by comment_count desc", countQuery =
      "SELECT count(*) FROM posts where posts.is_active = 1 "
          + "and posts.time < date(now()) \n"
          + "and posts.moderation_status = 'ACCEPTED'", nativeQuery = true)
  Page<Post> getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByPostVotes(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);


  @Query(value = "SELECT posts.*\n"
      + "FROM posts\n"
      + "         LEFT JOIN\n"
      + "     (SELECT post_id, COUNT(post_id) as votes_count\n"
      + "      FROM post_votes\n"
      + "      WHERE value = 1\n"
      + "      GROUP BY post_id)\n"
      + "         AS votes ON posts.id = votes.post_id\n"
      + "WHERE posts.is_active = :isActive\n"
      + "  AND posts.time < :time\n"
      + "  AND posts.moderation_status = :moderationStatus\n"
      + "ORDER BY votes_count DESC", countQuery =
      "SELECT count(*) FROM posts where posts.is_active = 1 "
          + "and posts.time < date(now()) \n"
          + "and posts.moderation_status = 'ACCEPTED'", nativeQuery = true)
  Page<Post> getAllByIsActiveAndModerationStatusAndTimeLessThan(
      @Param(value = "isActive") Byte isActive, @Param(value = "time") Date time,
      @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);
}
