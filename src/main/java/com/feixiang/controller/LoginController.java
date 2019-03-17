package com.feixiang.controller;

import com.feixiang.Common.JSONUtils;
import com.feixiang.Common.View;
import com.feixiang.annotation.Controller;
import com.feixiang.annotation.RequestMapping;
import com.feixiang.annotation.ResponseBody;
import com.feixiang.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lidaofei
 * @Date: 2019/3/17 20:58
 */
@Controller
public class LoginController {

    @RequestMapping(value = "user/login")
    public View login(HttpServletRequest request, HttpServletResponse response){
        System.out.println("==login start==");
        View view = new View("/login.jsp","forward");
        return view;
    }

    @RequestMapping(value = "user/index")
    public View index(HttpServletRequest request,HttpServletResponse response){
        System.out.println("==index start==");
        View view = new View("/index.jsp","forward");
        request.setAttribute("content",request.getParameter("username"));
        return view;
    }

    @RequestMapping(value = "user/json")
    @ResponseBody
    public Object json(User param,HttpServletRequest request,HttpServletResponse response){
        System.out.println("==index start==");
        try {
            System.out.println(JSONUtils.obj2json(param));
        } catch (Exception e) {
            e.printStackTrace();
        }
        View view = new View();
        view.setData(param);
        return view;
    }
}
