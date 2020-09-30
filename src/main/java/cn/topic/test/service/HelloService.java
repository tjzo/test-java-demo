package cn.topic.test.service;

import com.google.gson.Gson;

import java.util.Map;

public class HelloService {

    public long nullToZero(String x) {
        return x == null ? 0 : Long.parseLong(x);
    }

    public long add(long a, long b) {
        return a + b;
    }

    public long add(String a, String b) {
        return add(nullToZero(a), nullToZero(b));
    }

    public String[] split(String s, String reg) {
        return s.split(reg);
    }

    public Object clone(Object obj) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(obj), obj.getClass());
    }

    public Object getFromMap(Map<?, ?> map, Object key) {
        return map.get(key);
    }

    public void sleep(Long secs) throws InterruptedException {
        Thread.sleep(secs);
    }
}
