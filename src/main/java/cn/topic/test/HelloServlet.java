package cn.topic.test;

import cn.topic.test.service.HelloService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet {

    @Inject
    HelloService helloService;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String a = request.getParameter("a");
        String b = request.getParameter("b");
        long sum = helloService.add(a, b);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.println(sum);
    }
}