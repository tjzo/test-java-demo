package cn.topic.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestHelloServlet {

    static Server server;
    static HttpClient httpClient;

    @BeforeClass
    public static void setUp() throws Exception {
        httpClient = new DefaultHttpClient();
        server = new Server(80);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(
                new GuiceServletListener().getInjector().getInstance(HelloServlet.class)), "/hello");
        server.setHandler(handler);
        server.start();
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        server.stop();
    }

    @Test
    public void test() throws Exception {
        HttpGet request = new HttpGet("http://127.0.0.1/hello?a=1&b=2");
        HttpResponse response = httpClient.execute(request);
        try (Scanner scanner = new Scanner(response.getEntity().getContent())) {
            assertEquals("3", scanner.nextLine());
            assertFalse(scanner.hasNext());
        }
    }
}
