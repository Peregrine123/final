package com.jiyu.cache;

import com.jiyu.dao.BlogArticleDAO;
import com.jiyu.dao.CategoryDAO;
import com.jiyu.dao.CollectionDAO;
import com.jiyu.dao.MovieDAO;
import com.jiyu.pojo.BlogArticle;
import com.jiyu.pojo.BlogPage;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Collection;
import com.jiyu.pojo.Movie;
import com.jiyu.service.BlogArticleService;
import com.jiyu.service.CollectionService;
import com.jiyu.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
class CacheLayerTest {
    @Autowired
    MovieService movieService;
    @Autowired
    MovieDAO movieDAO;
    @Autowired
    CollectionService collectionService;
    @Autowired
    CollectionDAO collectionDAO;
    @Autowired
    BlogArticleService blogArticleService;
    @Autowired
    BlogArticleDAO blogArticleDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        collectionDAO.deleteAll();
        movieDAO.deleteAll();
        blogArticleDAO.deleteAll();
        categoryDAO.deleteAll();
        clearCaches();
    }

    @Test
    void movieListCacheAndEvict() {
        Category category = categoryDAO.save(newCategory("Action"));
        movieService.addOrUpdate(newMovie(category, "m1"));

        List<Movie> first = movieService.list();
        assertThat(first).hasSize(1);

        movieDAO.save(newMovie(category, "m2"));
        List<Movie> cached = movieService.list();
        assertThat(cached).hasSize(1);

        movieService.addOrUpdate(newMovie(category, "m3"));
        List<Movie> refreshed = movieService.list();
        assertThat(refreshed).hasSize(3);
    }

    @Test
    void collectionByCategoryCacheAndEvict() {
        Category category = categoryDAO.save(newCategory("Drama"));
        collectionService.addOrUpdate(newCollection(category, "c1"));

        List<Collection> first = collectionService.listByCategory(category.getId());
        assertThat(first).hasSize(1);

        collectionDAO.save(newCollection(category, "c2"));
        List<Collection> cached = collectionService.listByCategory(category.getId());
        assertThat(cached).hasSize(1);

        collectionService.addOrUpdate(newCollection(category, "c3"));
        List<Collection> refreshed = collectionService.listByCategory(category.getId());
        assertThat(refreshed).hasSize(3);
    }

    @Test
    void blogListAndDetailCache() {
        BlogArticle article = blogArticleDAO.save(newArticle("A1"));

        BlogPage firstList = blogArticleService.list(0, 10);
        assertThat(firstList.getTotalElements()).isEqualTo(1);

        BlogArticle cachedDetail = blogArticleService.findById(article.getId());
        assertThat(cachedDetail.getArticleTitle()).isEqualTo("A1");

        blogArticleDAO.save(newArticle("A2"));
        BlogPage cachedList = blogArticleService.list(0, 10);
        assertThat(cachedList.getTotalElements()).isEqualTo(1);

        BlogArticle updated = blogArticleDAO.findById(article.getId());
        updated.setArticleTitle("A1-updated");
        blogArticleDAO.save(updated);
        BlogArticle stillCachedDetail = blogArticleService.findById(article.getId());
        assertThat(stillCachedDetail.getArticleTitle()).isEqualTo("A1");

        blogArticleService.addOrUpdate(updated);
        BlogArticle refreshed = blogArticleService.findById(article.getId());
        assertThat(refreshed.getArticleTitle()).isEqualTo("A1-updated");
    }

    private void clearCaches() {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    private Category newCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private Movie newMovie(Category category, String title) {
        Movie movie = new Movie();
        movie.setCategory(category);
        movie.setTitle(title);
        movie.setCover("cover");
        movie.setCast("cast");
        movie.setDate("2025");
        movie.setPress("press");
        movie.setSummary("summary");
        return movie;
    }

    private Collection newCollection(Category category, String title) {
        Collection collection = new Collection();
        collection.setCategory(category);
        collection.setTitle(title);
        collection.setCover("cover");
        collection.setCast("cast");
        collection.setDate("2025");
        collection.setPress("press");
        collection.setSummary("summary");
        return collection;
    }

    private BlogArticle newArticle(String title) {
        BlogArticle article = new BlogArticle();
        article.setArticleTitle(title);
        article.setArticleContentHtml("<p>content</p>");
        article.setArticleContentMd("content");
        article.setArticleAbstract("abstract");
        article.setArticleCover("cover");
        article.setArticleDate(new Date(System.currentTimeMillis()));
        return article;
    }
}
