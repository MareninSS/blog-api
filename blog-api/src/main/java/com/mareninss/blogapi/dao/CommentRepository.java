package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<PostComment,Integer> {

}
