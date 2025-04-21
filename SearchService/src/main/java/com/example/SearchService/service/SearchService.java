package com.example.SearchService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void addTagCache(Long productId, List<String> tags) {
        SetOperations<String, String> ops = stringRedisTemplate.opsForSet();

        tags.forEach(tag -> {
            ops.add(tag, productId.toString());
        });
    }

    public void removeTagCache(Long productId, List<String> tags) {
        SetOperations<String, String> ops = stringRedisTemplate.opsForSet();

        tags.forEach(tag -> {
            ops.remove(tag, productId.toString());
        });
    }

    public List<Long> getProductIdsByTag(String tag) {
        SetOperations<String, String> ops = stringRedisTemplate.opsForSet();
        return ops.members(tag).stream()
                .map(Long::valueOf)
                .toList();
    }
}
