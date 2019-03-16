package com.feixiang.handler;

import com.feixiang.Common.RequestMappingMap;
import com.feixiang.Common.View;
import com.feixiang.annotation.Controller;
import com.feixiang.annotation.RequestMapping;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {

   //@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            //1.读取需要扫描的目录
            String scanPackage = config.getInitParameter("scanPackage");
            String scanPachageDirName = scanPackage.replace('.', '/');
            Enumeration<URL> dirUrls = Thread.currentThread().getContextClassLoader().getResources(scanPachageDirName);
            Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
            while (dirUrls.hasMoreElements()) {
                URL dirUrl = dirUrls.nextElement();
                String protocol = dirUrl.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(dirUrl.getFile(), "UTF-8");
                    File file = new File(filePath);
                    if(!file.exists() || !file.isDirectory()){
                        return;
                    }
                    File[] files = file.listFiles(new FileFilter(){
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".class");
                        }
                    });
                    //2.读取目录class文件
                    for(File item : files){
                        String className = item.getName().substring(0, item.getName().length() - 6);
                        classSet.add(Thread.currentThread().getContextClassLoader().loadClass(scanPackage+"."+className));
                    }
                    //3.读取class文件里的RequestMapping值
                    for(Class item : classSet){
                        if(item.isAnnotationPresent(Controller.class)){
                            Method[] methods = item.getDeclaredMethods();
                            for(Method method : methods){
                                if(method.isAnnotationPresent(RequestMapping.class)){
                                    String anValue = method.getAnnotation(RequestMapping.class).value();
                                    //4.放入Map<String,Class>静态集合中
                                    RequestMappingMap.setReqMappingMap(anValue,item);
                                }
                            }

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        this.doDispatcher(req,resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        this.doDispatcher(req,resp);
    }

    private void doDispatcher(HttpServletRequest request,HttpServletResponse response){
        try {
            //1.获取地址值
            String contextPath = request.getContextPath();
            String requestURI = request.getRequestURI();
            //2.解析出Mapping值
            String replacePath = requestURI.replace(contextPath + "/", "");
            String mappingValue = replacePath.substring(0, replacePath.lastIndexOf("."));
            //3.查找RequestMappingMap的class值
            Class clazz = RequestMappingMap.getClazz(mappingValue);
            //4.查找出class中对应的方法
            Method[] methods = clazz.getDeclaredMethods();
            Method method = null;
            for(Method item : methods){
                String anValue = item.getAnnotation(RequestMapping.class).value();
                if(mappingValue.equals(anValue)){
                    method = item;
                    break;
                }
            }
            //5.执行方法
            if(!clazz.isInterface()){
                Object clazzIns = clazz.newInstance();
                Object reObj = method.invoke(clazzIns, request, response);
                //6.跳转页面
                if(reObj != null){
                    View view = (View) reObj;
                    if("forward".equals(view.getDispatchAction())){
                        request.getRequestDispatcher(view.getUrl()).forward(request,response);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
