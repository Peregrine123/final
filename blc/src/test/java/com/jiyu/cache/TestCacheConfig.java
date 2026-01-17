package com.jiyu.cache;

import com.jiyu.config.CacheNames;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@EnableCaching
public class TestCacheConfig {
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                CacheNames.MOVIES_LIST,
                CacheNames.MOVIES_BY_CATEGORY,
                CacheNames.COLLECTIONS_LIST,
                CacheNames.COLLECTIONS_BY_CATEGORY,
                CacheNames.CATEGORIES_LIST,
                CacheNames.CATEGORIES_BY_ID,
                CacheNames.BLOG_LIST,
                CacheNames.BLOG_DETAIL
        );
    }
}
