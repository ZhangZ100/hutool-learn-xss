package com.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Z100
 * @create 2022-04-16 22:11
 * @desc
 **/
@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(String name){
        return "hello, springboot! your name is "+name;
    }
}
