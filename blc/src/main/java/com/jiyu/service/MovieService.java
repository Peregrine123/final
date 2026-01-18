package com.jiyu.service;

import com.jiyu.config.CacheNames;
import com.jiyu.dao.MovieDAO;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    MovieDAO movieDAO;
    @Autowired
    CategoryService categoryService;

    @Cacheable(cacheNames = CacheNames.MOVIES_LIST)
    public List<Movie> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return movieDAO.findAll(sort);
    }

    //当数据库与id对应的主键存在时更新数据，当主键不存在时插入数据
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.MOVIES_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.MOVIES_BY_CATEGORY, allEntries = true)
    })
    public void addOrUpdate(Movie movie) {
        normalizeMovie(movie);
        // Guardrail: avoid accidental duplicate inserts from the admin "add/import" flow.
        // If a movie with the same (title, date, press) already exists, treat the request as an update.
        if (movie != null && movie.getId() <= 0) {
            Optional<Movie> existing = findExistingByKey(movie);
            existing.ifPresent(m -> movie.setId(m.getId()));
        }
        movieDAO.save(movie);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.MOVIES_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.MOVIES_BY_CATEGORY, allEntries = true)
    })
    public void deleteById(int id) {
        movieDAO.deleteById(id);
    }

    @Cacheable(cacheNames = CacheNames.MOVIES_BY_CATEGORY, key = "#cid")
    public List<Movie> listByCategory(int cid) {
        Category category = categoryService.get(cid);
        return movieDAO.findAllByCategory(category);
    }

    public List<Movie> Search(String keywords) {
        return movieDAO.findAllByTitleLikeOrCastLike('%' + keywords + '%', '%' + keywords + '%');
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void normalizeMovie(Movie movie) {
        if (movie == null) {
            return;
        }
        movie.setTitle(trimToNull(movie.getTitle()));
        movie.setCast(trimToNull(movie.getCast()));
        movie.setDate(trimToNull(movie.getDate()));
        movie.setPress(trimToNull(movie.getPress()));
        movie.setSummary(trimToNull(movie.getSummary()));
        movie.setCover(trimToNull(movie.getCover()));
    }

    private Optional<Movie> findExistingByKey(Movie movie) {
        if (movie == null) {
            return Optional.empty();
        }
        String title = trimToNull(movie.getTitle());
        if (title == null) {
            return Optional.empty();
        }

        String date = trimToNull(movie.getDate());
        String press = trimToNull(movie.getPress());
        if (date != null && press != null) {
            return movieDAO.findFirstByTitleAndDateAndPress(title, date, press);
        }
        if (date != null) {
            return movieDAO.findFirstByTitleAndDate(title, date);
        }
        return movieDAO.findFirstByTitle(title);
    }

}
