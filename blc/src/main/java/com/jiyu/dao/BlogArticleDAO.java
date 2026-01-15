package com.jiyu.dao;

import com.jiyu.pojo.BlogArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogArticleDAO extends JpaRepository<BlogArticle,Integer> {
    BlogArticle findById(int id);
}
