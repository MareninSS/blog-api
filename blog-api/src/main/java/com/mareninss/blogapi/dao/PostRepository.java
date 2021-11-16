package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.ModerationStatusEnum;
import com.mareninss.blogapi.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

  List<Post> getAllByIsActiveAndModerationStatus(Byte isActive,
      ModerationStatusEnum moderationStatus);
}
