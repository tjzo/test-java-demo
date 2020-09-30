package cn.topic.test.service;

import cn.topic.test.RedisClient;

public class ServiceB {
    RedisClient redisClient1;
    RedisClient redisClient2;

    public String get1(String key) {
        return redisClient1.get(key);
    }

    public String get2(String key) {
        return redisClient2.get(key);
    }
}
