package cn.topic.test.service;

import cn.topic.test.RedisClient;

import javax.inject.Inject;

public class RedisService {

    @Inject
    RedisClient redisClient;

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public String set(String key, String value, long ex) {
        return redisClient.set(key, value, ex);
    }

    public String get(String key) {
        return redisClient.get(key);
    }
}
