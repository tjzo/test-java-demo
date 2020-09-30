package cn.topic.test;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.FileNotFoundException;
import java.net.URL;

public class LoadTest {
    static StandardJMeterEngine engine;

    static Server server;

    @Rule
    public Timeout timeout = new Timeout(5 * 60 * 1000);

    @BeforeClass
    public static void setUp() throws Exception {
        // Prepare server
        server = new Server(80);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(
                new GuiceServletListener().getInjector().getInstance(HelloServlet.class)), "/hello");
        server.setHandler(handler);
        server.start();

        // Prepare JMeter
        engine = new StandardJMeterEngine();

        // 读取配置文件
        URL url = LoadTest.class.getClassLoader().getResource("jmeter.properties");
        if (url == null) {
            throw new FileNotFoundException("jmeter.properties not found!");
        }
        JMeterUtils.loadJMeterProperties(url.getPath());

        // 创建测试计划
        TestPlan testPlan = new TestPlan("Simple test");

        // 创建http请求收集器
        HTTPSamplerProxy sampler = createHTTPSamplerProxy();

        // 创建循环控制器
        LoopController loopController = createLoopController();

        // 创建线程组
        ThreadGroup threadGroup = createThreadGroup();

        // 线程组设置循环控制
        threadGroup.setSamplerController(loopController);

        // 将测试计划添加到测试配置树种
        HashTree testPlanTree = new HashTree();
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);

        // 将http请求采样器添加到线程组下
        threadGroupHashTree.add(sampler);

        // 增加结果收集
        Summariser summer = new Summariser("Simple summariser");
        ResultCollector logger = new ResultCollector(summer);
        testPlanTree.add(testPlanTree.getArray(), logger);

        // 配置jmeter
        engine.configure(testPlanTree);
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        server.stop();
    }

    public static ThreadGroup createThreadGroup() {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Simple thread group");
        threadGroup.setNumThreads(10); // 总的线程数量
        threadGroup.setRampUp(0); // 启动时间，单位秒，参考https://cloud.tencent.com/developer/news/24366
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());

        threadGroup.setScheduler(true); // 是否使用Scheduler，参考https://zhuanlan.zhihu.com/p/142718347
        threadGroup.setDuration(60); // Duration和delay只有设置了Scheduler才生效。
        threadGroup.setDelay(0);
        return threadGroup;
    }

    public static LoopController createLoopController() {
        LoopController loopController = new LoopController();
        loopController.setLoops(1000);
        loopController.setContinueForever(false);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.initialize();
        return loopController;
    }

    public static HTTPSamplerProxy createHTTPSamplerProxy() {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setProperty("Content-Type", "multipart/form-data");
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setDomain("127.0.0.1");
        httpSamplerProxy.setPort(80);
        httpSamplerProxy.setPath("/hello");
        httpSamplerProxy.setMethod("GET");
        httpSamplerProxy.setConnectTimeout("500");
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setHeaderManager(headerManager);
        return httpSamplerProxy;
    }

    @Test
    @Ignore
    public void test() throws Exception {
        engine.run();
    }
}
