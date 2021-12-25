package com.study.inf.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class accountController {

    @GetMapping(value = {"/", "sign-up"})
    public String signUpForm(Model model){
        System.out.println("test");
        return "account/sign-up";
    }
    
}
 