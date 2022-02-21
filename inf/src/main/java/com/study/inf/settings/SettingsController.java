package com.study.inf.settings;

import com.study.inf.account.Account;
import com.study.inf.account.CurrentUser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
* 프로필 수정
*/

@Controller
public class SettingsController {

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){

        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        
        return "settings/profile";

    }
    
}
