package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j  // Enable SLF4J logging for this class
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String key, String value, Duration duration) {
        // Log the action of saving data to Redis
        log.info("Saving key: '{}' with value: '{}' for duration: {}", key, value, duration);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value, duration);

        // Log confirmation of saving
        log.info("Successfully saved key: '{}' to Redis.", key);
    }

    @Override
    public String get(String key) {
        // Log the action of retrieving data from Redis
        log.info("Retrieving value for key: '{}'", key);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String value = operations.get(key);

        // Log the retrieved value
        log.info("Retrieved value for key '{}': {}", key, value);
        return value;
    }

    @Override
    public void delete(String key) {
        // Log the action of deleting data from Redis
        log.info("Deleting key: '{}'", key);

        redisTemplate.delete(key);

        // Log confirmation of deletion
        log.info("Successfully deleted key: '{}' from Redis.", key);
    }

    @Override
    public Boolean isExist(String key) {
        // Log the action of checking existence in Redis
        log.info("Checking existence for key: '{}'", key);

        Boolean exists = redisTemplate.hasKey(key);

        // Log the existence status
        log.info("Key '{}' exists: {}", key, exists);
        return exists;
    }
}
