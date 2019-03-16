package com.feixiang.controller;

import com.feixiang.Common.View;
import com.feixiang.annotation.Controller;
import com.feixiang.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @RequestMapping(value = "login")
    public View login(HttpServletRequest request,HttpServletResponse response){
        View view = new View("/login.jsp","forward");
        return view;
    }
}
