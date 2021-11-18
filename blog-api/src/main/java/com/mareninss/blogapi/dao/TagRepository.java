package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {

  List<Tag> findAllByNameIsContaining(String query);
}
