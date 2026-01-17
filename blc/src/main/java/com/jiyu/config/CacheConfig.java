package com.jiyu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${blc.cache.ttl.list-seconds:300}")
    private long listTtlSeconds;

    @Value("${blc.cache.ttl.detail-seconds:120}")
    private long detailTtlSeconds;

    @Value("${blc.cache.ttl.category-seconds:600}")
    private long categoryTtlSeconds;

    @Bean
    @ConditionalOnProperty(prefix = "blc.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
                .prefixCacheNameWith("blc:");

        Duration listTtl = Duration.ofSeconds(listTtlSeconds);
        Duration detailTtl = Duration.ofSeconds(detailTtlSeconds);
        Duration categoryTtl = Duration.ofSeconds(categoryTtlSeconds);

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put(CacheNames.MOVIES_LIST, baseConfig.entryTtl(listTtl));
        cacheConfigs.put(CacheNames.MOVIES_BY_CATEGORY, baseConfig.entryTtl(listTtl));
        cacheConfigs.put(CacheNames.COLLECTIONS_LIST, baseConfig.entryTtl(listTtl));
        cacheConfigs.put(CacheNames.COLLECTIONS_BY_CATEGORY, baseConfig.entryTtl(listTtl));
        cacheConfigs.put(CacheNames.CATEGORIES_LIST, baseConfig.entryTtl(categoryTtl));
        cacheConfigs.put(CacheNames.CATEGORIES_BY_ID, baseConfig.entryTtl(categoryTtl));
        cacheConfigs.put(CacheNames.BLOG_LIST, baseConfig.entryTtl(listTtl));
        cacheConfigs.put(CacheNames.BLOG_DETAIL, baseConfig.entryTtl(detailTtl));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(baseConfig.entryTtl(listTtl))
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "blc.cache", name = "enabled", havingValue = "false")
    public CacheManager noOpCacheManager() {
        return new NoOpCacheManager();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache get failed (cache={}, key={}); falling back to DB.", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Cache put failed (cache={}, key={}); ignoring.", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache evict failed (cache={}, key={}); ignoring.", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Cache clear failed (cache={}); ignoring.", cache.getName(), exception);
            }
        };
    }
}
