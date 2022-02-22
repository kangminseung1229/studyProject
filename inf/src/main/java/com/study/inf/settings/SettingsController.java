package com.study.inf.settings;

import javax.validation.Valid;

import com.study.inf.account.Account;
import com.study.inf.account.AccountRepository;
import com.study.inf.account.AccountService;
import com.study.inf.account.CurrentUser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/*
* 프로필 수정
*/

@Controller
@RequiredArgsConstructor
public class SettingsController {


    private static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    private static final String SETTINGS_PROFILE_URL = "/settings/profile";
    
    private final AccountService accountService;




    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){

        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        
        return SETTINGS_PROFILE_VIEW_NAME;

    }
    
    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model, RedirectAttributes attributes){
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;    
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message","프로필을 수정했습니다."); // redirect 시 사용, 한번쓰고 버려진다.

        return "redirect:" + SETTINGS_PROFILE_URL;

    }

}
