package com.jiyu.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    //Springboot内置了servlet容器，没有web.xml，要将ServletRegistrationBean注册进去
    //加入后台监控
    @Bean  //servlet的web.xml
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean =
                new ServletRegistrationBean<StatViewServlet>(new StatViewServlet(), "/druid/*");
        Map<String, String> initParas = new HashMap<String, String>();
        initParas.put("loginUsername", "admin");
        initParas.put("loginPassword", "123456");
        //允许谁能访问
        initParas.put("allow", "");//都可以
        //initParas.put("allow","localhost");//只允许本机访问
        //initParas.put("deny","");//黑名单，拒绝谁访问
        initParas.put("resetEnable", "false");//禁用HTML页面的Reset按钮
        bean.setInitParameters(initParas);
        return bean;
    }

    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        //阿里巴巴的过滤器
        bean.setFilter(new WebStatFilter());
        bean.addUrlPatterns("/*");
        Map<String, String> initParams = new HashMap<String, String>();
        //把不需要监控的过滤掉,这些不进行统计
        initParams.put("exclusions", "*.js,*.css,/druid/*");
        bean.setInitParameters(initParams);
        return bean;
    }
}

