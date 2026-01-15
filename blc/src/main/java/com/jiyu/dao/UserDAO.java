package com.jiyu.dao;

import com.jiyu.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDAO extends JpaRepository<User,Long> {
    User findByUsername(String username);

    User getByUsernameAndPassword(String username, String password);
}
