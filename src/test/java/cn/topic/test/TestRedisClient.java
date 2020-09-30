package cn.topic.test;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestRedisClient {

    @Mock
    RedisClient mockRedisClient;

    @Spy
    RedisClient spyRedisClient;

    @Captor
    ArgumentCaptor<String> keyCaptor;

    @Captor
    ArgumentCaptor<String> valueCaptor;

    @Captor
    ArgumentCaptor<Long> exCaptor;

    @Test
    public void testWithMockMethod() throws Exception {
        RedisClient redisClient = mock(RedisClient.class);
        when(redisClient.set("a", "b", 1L)).thenReturn("b");
        when(redisClient.set("b", "b", 1L)).thenThrow(new RuntimeException("This is an error"));
        when(redisClient.get("a")).thenReturn("b");
        when(redisClient.getCurrentTimestamp()).thenAnswer(invocation -> System.currentTimeMillis());

        assertEquals("b", redisClient.set("a", "b", 1L));
        try {
            redisClient.set("b", "b", 1L);
            fail("This method call should throw an exception");
        } catch (RuntimeException t) {
            assertEquals("This is an error", t.getMessage());
        }
        assertEquals("b", redisClient.get("a"));
        assertTrue(redisClient.getCurrentTimestamp() > 0);

        verify(redisClient).set("a", "b", 1L);
        verify(redisClient).set("b", "b", 1L);
        verify(redisClient, times(2)).set(anyString(), anyString(), anyLong());


        InOrder order = inOrder(redisClient);
        order.verify(redisClient).set("a", "b", 1L);
        order.verify(redisClient).set("b", "b", 1L);
        order.verify(redisClient).getCurrentTimestamp();
    }

    @Test
    public void testWithMockAnnotation() throws Exception {
        try {
            mockRedisClient.set("a", "b", 1L);
            fail("Should throw NPE");
        } catch (NullPointerException exception) {
            // not initialized
        }

        MockitoAnnotations.initMocks(this);
        mockRedisClient.set("a", "b", 1L);
        verify(mockRedisClient).set("a", "b", 1L);
        assertFalse(mockRedisClient.getClass().equals(RedisClient.class));

        verify(mockRedisClient).set(keyCaptor.capture(), valueCaptor.capture(), exCaptor.capture());
        assertEquals("a", keyCaptor.getValue());
        assertEquals("b", valueCaptor.getValue());
        assertEquals(Long.valueOf(1L), exCaptor.getValue());

        try {
            spyRedisClient.set("A", "B", 2L);
            fail("Should throw exception");
        } catch (UnsupportedOperationException e) {
            // excepted
        }

        try {
            spyRedisClient.get("A");
            fail("Should throw exception");
        } catch (UnsupportedOperationException e) {
            // excepted
        }

        try {
            spyRedisClient.getCurrentTimestamp();
            fail("Should throw exception");
        } catch (UnsupportedOperationException e) {
            // excepted
        }

        try {
            spyRedisClient.destroy();
            fail("Should throw exception");
        } catch (UnsupportedOperationException e) {
            // excepted
        }

        try {
            spyRedisClient.cleanUp();
            fail("Should throw exception");
        } catch (UnsupportedOperationException e) {
            // excepted
        }

        verify(spyRedisClient).set("A", "B", 2L);
        assertFalse(spyRedisClient.getClass().equals(RedisClient.class));

        verify(spyRedisClient).set(keyCaptor.capture(), valueCaptor.capture(), exCaptor.capture());
        assertEquals("A", keyCaptor.getValue());
        assertEquals("B", valueCaptor.getValue());
        assertEquals(Long.valueOf(2L), exCaptor.getValue());

        assertEquals(Lists.newArrayList("a", "A"), keyCaptor.getAllValues());
        assertEquals(Lists.newArrayList("b", "B"), valueCaptor.getAllValues());
        assertEquals(Lists.newArrayList(1L, 2L), exCaptor.getAllValues());

        InOrder order = inOrder(mockRedisClient, spyRedisClient);
        order.verify(mockRedisClient).set("a", "b", 1L);
        order.verify(spyRedisClient).set("A", "B", 2L);
        order.verify(spyRedisClient).get("A");
        order.verify(spyRedisClient).getCurrentTimestamp();
        order.verify(spyRedisClient).destroy();
        order.verify(spyRedisClient).cleanUp();
    }
}
