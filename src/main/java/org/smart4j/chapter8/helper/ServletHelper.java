package org.smart4j.chapter8.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet 助手类
 */
public final class ServletHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletHelper.class);

    /**
     * 使每个线程独自拥有一份 ServletHelper 实例
     */
    private static final ThreadLocal<ServletHelper> SERVLET_HELPER_THREAD_LOCAL = new ThreadLocal<ServletHelper>();

    private HttpServletRequest request;
    private HttpServletResponse response;

    private ServletHelper(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    /**
     * 初始化
     */
    public static void init(HttpServletRequest request,HttpServletResponse response){
        SERVLET_HELPER_THREAD_LOCAL.set(new ServletHelper(request,response));
    }

    /**
     * 销毁
     */
    public static void destroy(){
        SERVLET_HELPER_THREAD_LOCAL.remove();
    }

    /**
     * 获取Request对象
     */
    public static HttpServletRequest getRequest(){
        return SERVLET_HELPER_THREAD_LOCAL.get().request;
    }

    /**
     * 获取Response对象
     */
    public static HttpServletResponse getResponse(){
        return SERVLET_HELPER_THREAD_LOCAL.get().response;
    }

    /**
     * 获取Session对象
     */
    public static HttpSession getSession(){
        return getRequest().getSession();
    }

}
