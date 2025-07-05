package com.ott.cachegrid.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public CacheService(RedisTemplate<String, String> template){
        this.redisTemplate = template;
    }

    public void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public Boolean exists(String key) {
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }

    public void delete(String key) {
        if(this.exists(key)) {
            this.redisTemplate.delete(key);
        }
    }

    public void update(String key, String value) {
        this.delete(key);
        this.set(key, value);
    }
}
