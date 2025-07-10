package com.ott.cachegrid.auth;

import com.ott.cachegrid.common.CacheTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;

@Service
public class APIKeyService {

    private final RedisTemplate<String, APIKeyRecord> redisTemplate;

    public APIKeyService(CacheTemplate cacheTemplate, RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = cacheTemplate.apiKeyRedisTemplate(redisConnectionFactory);
    }

    public boolean validateApiKey(String projID, String presentedKey) {
        return this.fetchAPIRecord(projID)
                .map(rec -> constantTimeEquals(rec.key(), presentedKey) && notExpired(rec.exp())).orElse(false);
    }

    public Optional<APIKeyRecord> fetchAPIRecord(String projID) {
        return Optional.ofNullable(this.redisTemplate.opsForValue().get(projID));
    }

    private static boolean notExpired(Instant expiresAt) {
        return expiresAt == null || expiresAt.isAfter(Instant.now());
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
