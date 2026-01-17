package com.jiyu.controller;

import com.jiyu.pojo.BlogArticle;
import com.jiyu.pojo.BlogPage;
import com.jiyu.result.Result;
import com.jiyu.result.ResultFactory;
import com.jiyu.service.BlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

//带admin表示需要权限
@RestController
public class BlogController {
    @Autowired
    BlogArticleService blogArticleService;

    @PostMapping("api/admin/article")
    public Result saveArticle(@RequestBody BlogArticle article) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        article.setArticleDate(java.sql.Date.valueOf(df.format(new Date())));//string=>Date   文章发布时间在后端自动加上去（前端也可）
        blogArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @GetMapping("/api/article/{size}/{page}")
    public BlogPage listArticles(@PathVariable("size") int size, @PathVariable("page") int page) {
        return blogArticleService.list(page - 1, size);//page要减一
    }

    @GetMapping("/api/article/{id}")
    public BlogArticle getOneArticle(@PathVariable("id") int id) {
        return blogArticleService.findById(id);
    }

    @DeleteMapping("/api/admin/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
        blogArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
