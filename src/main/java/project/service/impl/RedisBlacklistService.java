package project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_KEY_PREFIX = "jwt_blacklist";

    // Đưa token vào blacklist của redis kèm thời gian sống của token theo mili giây
    public void blacklistToken(String token , Long remainingTimeMillis){
        if (remainingTimeMillis > 0){
            String key = REDIS_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(key,"true",remainingTimeMillis, TimeUnit.MILLISECONDS);
        }
    }

    // Kiểm tra token có trong redis k
    public boolean isCheckBlacklist(String token){
        String key = REDIS_KEY_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
