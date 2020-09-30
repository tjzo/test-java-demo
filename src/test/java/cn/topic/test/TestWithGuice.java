package cn.topic.test;

import cn.topic.test.service.RedisService;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestWithGuice {

    static RedisService redisService;

    @BeforeClass
    public static void setUp() {
        Injector injector = Guice.createInjector(binder -> {
            binder.bind(RedisClient.class).to(FakeRedisClient.class);
        });
        redisService = injector.getInstance(RedisService.class);
    }

    @AfterClass
    public static void destroy() {
        redisService.getRedisClient().destroy();
    }

    @After
    public void cleanUp() {
        redisService.getRedisClient().cleanUp();
    }

    @Test
    public void test() throws Exception {
        assertEquals("b", redisService.set("a", "b", 200L));
        assertEquals("b", redisService.get("a"));
        Thread.sleep(300L);
        assertNull(redisService.get("a"));
    }

    static class FakeRedisClient extends RedisClient {

        Map<String, String> map = Maps.newHashMap();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        @Override
        public String set(String key, String value, long ex) {
            map.put(key, value);
            executorService.schedule(() -> map.remove(key), ex, TimeUnit.MILLISECONDS);
            return value;
        }

        @Override
        public String get(String key) {
            return map.get(key);
        }

        @Override
        public void destroy() {
            executorService.shutdownNow();
        }

        @Override
        public void cleanUp() {
            map.clear();
        }
    }
}
