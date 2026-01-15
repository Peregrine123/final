package com.jiyu.dao;

import com.jiyu.pojo.Category;
import com.jiyu.pojo.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionDAO extends JpaRepository<Collection, Integer> {
    List<Collection> findAllByCategory(Category category);
    List<Collection> findAllByTitleLikeOrCastLike(String keyword1, String keyword2);
}
