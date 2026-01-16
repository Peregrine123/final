package com.jiyu;

import com.jiyu.result.ResultCode;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
@ActiveProfiles("test")
class BlcApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(ResultCode.SUCCESS);
    }

    //测试加密
    @Test
    void test(){
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("md5", "jiyu", salt, times).toString();
        System.out.println("jiyu".equals(encodedPassword));
    }

    //生成随机salt
    @Test
    void test02(){
        System.out.println("1: "+new SecureRandomNumberGenerator().nextBytes().toString());
        System.out.println("2: "+new SecureRandomNumberGenerator().nextBytes().toHex());
    }

    @Test
    void test03() throws ParseException {
        System.out.println(new Date().getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println("/"+df.format(new Date()));// new Date()为获取当前系统时间
        System.out.println("df.parse(df.format(new Date())) "+df.parse(df.format(new Date())));
        System.out.println(LocalDateTime.now());

        Timestamp d= new Timestamp(System.currentTimeMillis());
        System.out.println(df.format(d));


    }
}
