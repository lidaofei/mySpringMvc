package com.feixiang.handler;

import com.feixiang.Common.ConverterMap;
import com.feixiang.Common.JSONUtils;
import com.feixiang.Common.RequestMappingMap;
import com.feixiang.Common.View;
import com.feixiang.annotation.Controller;
import com.feixiang.annotation.RequestMapping;
import com.feixiang.annotation.ResponseBody;
import com.feixiang.converter.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: lidaofei
 * @Date: 2019/3/17 20:58
 */
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
            //5.初始化类型转化器
            if(config.getInitParameter("java.math.BigDecimal")!=null){
                ConverterMap.setConverterMap("java.math.BigDecimal", Class.forName(config.getInitParameter("java.math.BigDecimal")));
            } else {
                ConverterMap.setConverterMap("java.math.BigDecimal",BigdecimalConverter.class);
            }
            config.getInitParameter("java.math.BigDecimal");
            ConverterMap.setConverterMap("java.lang.String",StringConverter.class);
            ConverterMap.setConverterMap("java.lang.Long",LongConverter.class);
            ConverterMap.setConverterMap("java.util.Date",DateConverter.class);
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
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object reObj = null;
                if(parameterTypes.length==0){
                    reObj = method.invoke(clazzIns);
                } else {
                    //找出方法上的自定义对象
                    Class<?> paramClazz = null;
                    for(Class<?> paramItem : parameterTypes){
                        if(!paramItem.equals(HttpServletRequest.class)
                                && !paramItem.equals(HttpServletResponse.class)){
                            paramClazz = paramItem;
                            break;
                        }
                    }
                    if(paramClazz == null){
                        reObj = method.invoke(clazzIns, request, response);
                    } else {
                        //获取request body里面的值
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                        char[] c = new char[1024];
                        int len;
                        while ((len = br.read(c)) != -1){
                            sb.append(c,0,len);
                        }
                        //关闭流
                        br.close();

                        Map<String, Object> requestMap = null;
                        if(sb.length()>0){
                            requestMap = JSONUtils.json2map(sb.toString());
                        }
                        //给对象里属性赋值
                        Object paramObj = paramClazz.newInstance();
                        Field[] declaredFields = paramClazz.getDeclaredFields();
                        for(Field field : declaredFields){
                            field.setAccessible(true);
                            String fieldName = field.getName();
                            Object reqValObj = requestMap.get(field.getName());
                            if(reqValObj != null){
                                String reqVal = reqValObj.toString();
                                String fieldTypeName = field.getGenericType().getTypeName();
                                ConverterFactory converterF = (ConverterFactory) ConverterMap.getConerterClazz(fieldTypeName).newInstance();
                                field.set(paramObj,converterF.converter(reqVal));
                                /*if(fieldTypeName.endsWith("String")){
                                    field.set(paramObj,reqVal);
                                } else if(fieldTypeName.endsWith("Long")){
                                    field.set(paramObj,Long.valueOf(reqVal));
                                }*/
                            }
                        }
                        reObj = method.invoke(clazzIns,paramObj,request,response);
                    }
                }
                //6.跳转页面
                if(reObj != null){
                    View view = (View) reObj;
                    if("forward".equals(view.getDispatchAction())){
                        request.getRequestDispatcher(view.getUrl()).forward(request,response);
                    } else {
                        if(method.isAnnotationPresent(ResponseBody.class)){
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            out.append(JSONUtils.obj2json(reObj));
                            out.close();
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
