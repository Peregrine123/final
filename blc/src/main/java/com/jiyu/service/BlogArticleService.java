package com.jiyu.service;

import com.jiyu.dao.BlogArticleDAO;
import com.jiyu.pojo.BlogArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogArticleService {
    @Autowired
    BlogArticleDAO blogArticleDAO;

    public Page list(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");//降序，最新的在前面
        return  blogArticleDAO.findAll(PageRequest.of(page, size, sort));
    }

    public BlogArticle findById(int id) {
        return blogArticleDAO.findById(id);
    }

    public void addOrUpdate(BlogArticle article) {
        blogArticleDAO.save(article);
    }

    public void delete(int id) {
        blogArticleDAO.deleteById(id);
    }

}
