package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.ModerationStatusEnum;
import com.mareninss.blogapi.entity.Post;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

  Optional<Post> getAllByIsActiveAndModerationStatus(Byte isActive,
      ModerationStatusEnum moderationStatus);
}
