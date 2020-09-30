package cn.topic.test;

import cn.topic.test.service.HelloService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestHelloService {

    public static HelloService helloService;
    @Rule
    public MethodRule timeOut = new Timeout(1000);

    @BeforeClass
    public static void init() {
        // 初始化一些变量等
        helloService = new HelloService();
    }

    @AfterClass
    public static void tearUp() {
        // 所有测试完做的清理
    }

    @Before
    public void prepare() {
        // 每次测试前做的初始化
    }

    @After
    public void clear() {
        // 每次测试完做的清理
    }

    @Test
    public void testNullToZero() throws Exception {
        assertEquals(0, helloService.nullToZero(null));
        assertEquals(2, helloService.nullToZero("2"));
        try {
            long number = helloService.nullToZero("");
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // Excepted exception
        }
    }

    @Test
    public void testAdd() throws Exception {
        assertTrue(helloService.add(1, 2) == 3);
        assertFalse(helloService.add("2", "3") == 3);
    }

    @Test
    public void testSplit() throws Exception {
        assertArrayEquals(new String[]{"a", "bc", "def"}, helloService.split("a2bc37def", "\\d+"));
    }

    @Test
    public void testClone() throws Exception {
        TestObject obj = new TestObject("小明", 12);
        assertEquals(obj, helloService.clone(obj));
        assertNotSame(obj, helloService.clone(obj));
    }

    @Test
    public void testGetFromMap() throws Exception {
        Map<String, Long> map = new HashMap<>();
        map.put("a", 1L);
        assertNotNull(helloService.getFromMap(map, "a"));
        assertNull(helloService.getFromMap(map, "b"));
        assertEquals(1L, helloService.getFromMap(map, "a"));
    }

    @Test
    public void testSleep() throws Exception {
        helloService.sleep(500L);
    }

    @Test
    @Ignore
    public void testSleepFail() throws Exception {
        helloService.sleep(2000L);
    }

    static class TestObject {
        String name;
        int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return age == that.age &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

}
