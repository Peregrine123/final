package com.jiyu.service;

import com.jiyu.dao.UserDAO;
import com.jiyu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;



@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public boolean isExist(String username) {
        User user = getByUserName(username);
        return user != null;
    }

    public User getByUserName(String username) {
        return userDAO.findByUsername(username);
    }

    public User get(String username, String password){
        return userDAO.getByUsernameAndPassword(username, password);
    }

    public void add(User user) {
        userDAO.save(user);
    }


    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 加密新密码
            String password = user.getPassword();
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            int times = 2;
            String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
            user.setSalt(salt);
            user.setPassword(encodedPassword);
        } else {
            User existingUser = userDAO.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
                user.setSalt(existingUser.getSalt());
            } else {
                throw new IllegalArgumentException("用户不存在");
            }
        }
        userDAO.save(user);
    }

    public void delete(Long id) {
        userDAO.deleteById(id);
    }

    public User getCurrentUser(String username) {
        return userDAO.findByUsername(username);
    }

    // 新增的根据ID获取用户信息的方法
    public User getUserById(Long id) {
        return userDAO.findById(id).orElse(null);
    }
}
