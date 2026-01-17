package com.jiyu.service;

import com.jiyu.config.CacheNames;
import com.jiyu.dao.CategoryDAO;
import com.jiyu.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    @Cacheable(cacheNames = CacheNames.CATEGORIES_LIST)
    public List<Category> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    @Cacheable(cacheNames = CacheNames.CATEGORIES_BY_ID, key = "#id", unless = "#result == null")
    public Category get(int id) {
        Category c= categoryDAO.findById(id).orElse(null);
        return c;
    }
}
