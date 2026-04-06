package com.example.thymeleaf.CONTROLLER;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.thymeleaf.Model.User;


//Handle Method to Handle Variable Expressions

@Controller
public class UserController {
  
  @GetMapping("/variable-expression")
  public String ve(Model model) {
      User u=new User("Mary", "marie321@gmail.com", "Student", "Female");
      model.addAttribute("user",u);
      return "variable-expression";

  }
  // Handle Method to handle Selection Expressions
  @GetMapping("/selection-expression")
  public String se(Model model) {
      User u=new User("Mary", "marie321@gmail.com", "Student", "Female");
      model.addAttribute("user",u);
      return "selection-expression";

  }
  // Handle Method to handle Message Expressions
  @GetMapping("/message-expression")
  public String me() {
      return "message-expression";
  }
  // Handle Method to handle Link Expressions
   @GetMapping("/link-expression")
  public String le() {
      return "link-expression";

  }
  // Handle Method to handle Message Expressions
  @GetMapping("/fragment-expression")
  public String fe() {
      return "fragment-expression";
  }
  @GetMapping("/if-unless")
  public String iu(Model m) {
      User u=new User("Admin","Admin@gmail.com","Admin","Male");
       User u1=new User("Mary","Admin@gmail.com","Student","Female");
      List<User> li=new ArrayList<>();
      li.add(u);
      li.add(u1);
      m.addAttribute("users",li);
      return "if-unless";
  }
}
