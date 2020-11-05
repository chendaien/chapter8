package org.smart4j.chapter8.servlet;

import org.apache.commons.lang3.StringUtils;
import org.smart4j.chapter8.bean.Data;
import org.smart4j.chapter8.bean.Handler;
import org.smart4j.chapter8.bean.Param;
import org.smart4j.chapter8.bean.View;
import org.smart4j.chapter8.constant.ConfigConstant;
import org.smart4j.chapter8.helper.*;
import org.smart4j.chapter8.init.HelperLoader;
import org.smart4j.chapter8.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化Helper类
        HelperLoader.init();
        //获取ServletContext对象(用于注册Servlet)
        ServletContext servletContext = servletConfig.getServletContext();
        //获取注册JSP的Servlet
        ServletRegistration jspServletRegistration = servletContext.getServletRegistration("jsp");
        jspServletRegistration.addMapping(ConfigHelper.getAppJspPath()+"*");
        //注册静态资源的默认servlet
        ServletRegistration defaultServletRegistration = servletContext.getServletRegistration("default");
        defaultServletRegistration.addMapping(ConfigHelper.getAppAssetPath()+"*");
        //文件上传初始化
        UploadHelper.init(servletContext);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletHelper.init(req,resp);
        try {
            //获取请求方法与请求路径
            String requestMethod = req.getMethod();
            String requestPath = req.getPathInfo();
            if("/favicon.ico".equals(requestPath)){
                return;
            }
            //获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
            if(handler != null){
                //获取Controller类与Bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                //创建请求参数对象
                Param param;
                if(UploadHelper.isMultipart(req)){
                    param = UploadHelper.createParam(req);
                }else{
                    param = RequestHelper.createParam(req);
                }
                Method actionMethod = handler.getActionMethod();
                Object result;
                if(param.isEmpty()){
                    result = ReflectionUtil.invokeMethod(controllerBean,actionMethod);
                }else{
                    result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
                }
                if(result instanceof View){
                    handleViewResult((View)result,req,resp);
                }else if(result instanceof Data){
                    handleDataResult((Data)result,resp);
                }
            }
        }finally {
            ServletHelper.destroy();
        }
    }

    private void handleViewResult(View view,HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
        //返回JSP
        String path = view.getPath();
        if(StringUtil.isNotEmpty(path)){
            if(path.startsWith("/")){
                response.sendRedirect(request.getContextPath()+path);
            }else{
                Map<String,Object> model = view.getModel();
                for(Map.Entry<String,Object> entry:model.entrySet()){
                    request.setAttribute(entry.getKey(),entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
            }
        }
    }

    private void handleDataResult(Data data,HttpServletResponse response)throws IOException{
        //返回JSON数据
        Object model = data.getModel();
        if(model != null){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(JsonUtil.toJson(model));
            printWriter.flush();
            printWriter.close();
        }
    }

}
