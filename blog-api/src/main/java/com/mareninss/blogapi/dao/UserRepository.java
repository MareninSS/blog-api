package com.mareninss.blogapi.dao;

import com.mareninss.blogapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
