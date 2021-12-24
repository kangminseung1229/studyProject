package com.study.inf.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class accountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        return "account/sign-up";
    }
    
}
 