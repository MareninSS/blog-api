package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.PostVote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVotesRepository extends JpaRepository<PostVote, Integer> {

  Optional<PostVote> getByPostId(Integer postId);
}
