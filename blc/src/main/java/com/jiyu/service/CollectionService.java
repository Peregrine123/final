package com.jiyu.service;

import com.jiyu.dao.CollectionDAO;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {
    @Autowired
    CollectionDAO collectionDAO;
    @Autowired
    CategoryService categoryService;

    public List<Collection> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return collectionDAO.findAll(sort);
    }

    //当数据库与id对应的主键存在时更新数据，当主键不存在时插入数据
    public void addOrUpdate(Collection collection) {
        collectionDAO.save(collection);
    }

    public void deleteById(int id) {
        collectionDAO.deleteById(id);
    }

    public List<Collection> listByCategory(int cid) {
        Category category = categoryService.get(cid);
        return collectionDAO.findAllByCategory(category);
    }

    public List<Collection> Search(String keywords) {
        return collectionDAO.findAllByTitleLikeOrCastLike('%' + keywords + '%', '%' + keywords + '%');
    }

}
