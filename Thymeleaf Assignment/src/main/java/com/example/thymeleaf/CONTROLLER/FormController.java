package com.example.thymeleaf.CONTROLLER;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.thymeleaf.Model.userForm;

@Controller
public class FormController {
  
  @GetMapping("/register")
  public String userRegistraionPage(Model m){
    userForm uf=new userForm();
    m.addAttribute("userform", uf);
    List<String> listprofession=Arrays.asList("Developer","Tester","Architect");
    m.addAttribute("listprofession", listprofession);
    return "register-form";
  }
  @PostMapping("/register/save")
  public String submitForm(Model m,@ModelAttribute("userform") userForm uf){
    m.addAttribute("userform", uf);
    return "register-sucess";
  }
}
