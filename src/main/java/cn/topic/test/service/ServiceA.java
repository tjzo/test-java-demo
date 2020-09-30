package cn.topic.test.service;

import cn.topic.test.RedisClient;

public class ServiceA {
    RedisClient redisClient;

    public String get(String key) {
        return redisClient.get(key);
    }
}
