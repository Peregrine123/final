package com.jiyu.controller;

import com.jiyu.pojo.User;
import com.jiyu.result.Result;
import com.jiyu.result.ResultFactory;
import com.jiyu.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/api/login/check")
    @ResponseBody
    public Result loginCheck(@RequestParam String username) {
        //只能检测用户名是否正确，不能检测密码
        username = HtmlUtils.htmlEscape(username);

        boolean exist = userService.isExist(username);
        if (!exist) {
            String message = "用户名不存在";
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult(username);
    }

    //返回用户的角色
    @ResponseBody
    @GetMapping(value = "api/user/role")
    public Result roleCheck(@RequestParam String username){
        username = HtmlUtils.htmlEscape(username);
        User user = userService.getByUserName(username);
        String role = user.getRole();

        System.out.println(role);
        return ResultFactory.buildSuccessResult(role);
    }

    @PostMapping(value = "/api/login")
    @ResponseBody
    public Result login(@RequestBody User requestUser) {
        String username = requestUser.getUsername();
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, requestUser.getPassword());
        usernamePasswordToken.setRememberMe(true);//开启RememberMe
        try {
            //登录验证
            subject.login(usernamePasswordToken);
            return ResultFactory.buildSuccessResult(username);
        } catch (AuthenticationException e) {
            String message = "账号或密码错误";
            return ResultFactory.buildFailResult(message);
        }
    }

    @GetMapping("api/register/check")
    @ResponseBody
    public Result registerCheck(@RequestParam("username") String username) {

        username = HtmlUtils.htmlEscape(username);//Html编码转译,防止恶意注册

        boolean exist = userService.isExist(username);
        if (exist) {
            String message = "用户名已被使用";
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult(username);
    }

    @PostMapping("api/register")
    @ResponseBody
    public Result register(@RequestBody User user) {

        String username = user.getUsername();
        String password = user.getPassword();
        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);

        boolean exist = userService.isExist(username);
        if (exist) {
            String message = "用户名已被使用";
            return ResultFactory.buildFailResult(message);
        }

        // 随机生成长度24位的盐
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        user.setRole("normal");
        userService.add(user);

        return ResultFactory.buildSuccessResult(user);
    }

    @ResponseBody
    @GetMapping("api/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();//会清除 session、principals，并把 authenticated 设置为 false
        String message = "成功登出";
        return ResultFactory.buildSuccessResult(message);
    }

    @ResponseBody
    @GetMapping(value = "api/authentication")
    public String authentication(){
        return "身份认证成功";
    }



}