package com.study.inf.main;

import com.study.inf.account.Account;
import com.study.inf.account.CurrentUser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {

        // @CurrentUser 어노테이션에 의해 인증정보가 anonymousUser 이라면 account 는 null 이 된다.

        if (account != null) {
            model.addAttribute(account);

        }

        return "index";

    }
}
