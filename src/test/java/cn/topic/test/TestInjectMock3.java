package cn.topic.test;

import cn.topic.test.service.ServiceB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestInjectMock3 {

    @Mock
    RedisClient redisClient1;

    @Mock
    RedisClient redisClient2;

    @InjectMocks
    ServiceB service;

    @Test
    public void test() throws Exception {
        when(redisClient1.get(anyString())).thenAnswer(i -> i.getArgument(0).toString().toLowerCase());
        when(redisClient2.get(anyString())).thenAnswer(i -> i.getArgument(0).toString().toUpperCase());

        assertEquals("a", service.get1("A"));
        verify(redisClient1).get("A");
        verify(redisClient2, never()).get(anyString());

        assertEquals("B", service.get2("b"));
        verify(redisClient2).get("b");
    }
}
