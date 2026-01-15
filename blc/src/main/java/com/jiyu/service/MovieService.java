package com.jiyu.service;

import com.jiyu.dao.MovieDAO;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    MovieDAO movieDAO;
    @Autowired
    CategoryService categoryService;

    public List<Movie> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return movieDAO.findAll(sort);
    }

    //当数据库与id对应的主键存在时更新数据，当主键不存在时插入数据
    public void addOrUpdate(Movie movie) {
        movieDAO.save(movie);
    }

    public void deleteById(int id) {
        movieDAO.deleteById(id);
    }

    public List<Movie> listByCategory(int cid) {
        Category category = categoryService.get(cid);
        return movieDAO.findAllByCategory(category);
    }

    public List<Movie> Search(String keywords) {
        return movieDAO.findAllByTitleLikeOrCastLike('%' + keywords + '%', '%' + keywords + '%');
    }

}