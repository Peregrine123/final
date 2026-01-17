package com.jiyu.service;

import com.jiyu.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    @Autowired
    UserService userService;

    public User requireCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        String username = String.valueOf(principal);
        User user = userService.getByUserName(username);
        if (user == null) {
            // Should not happen in normal flows (principal comes from successful login),
            // but it protects against stale sessions / deleted users.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        return user;
    }
}

