package cn.topic.test;

import cn.topic.test.service.ServiceA;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestInjectMock1 {

    @Mock
    RedisClient redisClient;

    @InjectMocks
    ServiceA service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() throws Exception {
        when(redisClient.get(anyString())).thenAnswer(i -> i.getArgument(0).toString().toLowerCase());
        assertEquals("a", service.get("A"));
        verify(redisClient).get("A");
    }
}
