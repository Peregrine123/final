package com.jiyu.service;

import com.jiyu.config.CacheNames;
import com.jiyu.dao.BlogArticleDAO;
import com.jiyu.pojo.BlogArticle;
import com.jiyu.pojo.BlogPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogArticleService {
    @Autowired
    BlogArticleDAO blogArticleDAO;

    @Cacheable(cacheNames = CacheNames.BLOG_LIST, key = "'p:' + #page + ':s:' + #size")
    public BlogPage list(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");//降序，最新的在前面
        return BlogPage.from(blogArticleDAO.findAll(PageRequest.of(page, size, sort)));
    }

    @Cacheable(cacheNames = CacheNames.BLOG_DETAIL, key = "#id", unless = "#result == null")
    public BlogArticle findById(int id) {
        return blogArticleDAO.findById(id);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.BLOG_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.BLOG_DETAIL, key = "#article.id", condition = "#article != null && #article.id > 0")
    })
    public void addOrUpdate(BlogArticle article) {
        blogArticleDAO.save(article);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.BLOG_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.BLOG_DETAIL, key = "#id")
    })
    public void delete(int id) {
        blogArticleDAO.deleteById(id);
    }

}
