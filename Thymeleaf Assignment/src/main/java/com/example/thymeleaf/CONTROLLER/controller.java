package com.example.thymeleaf.CONTROLLER;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controller {

  @GetMapping("/helloworld")
  public String hw(Model m){
    m.addAttribute("message", "Hello World");
    return "hello-world";
  }
  
}
