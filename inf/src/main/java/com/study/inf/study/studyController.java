package com.study.inf.study;

import com.study.inf.account.CurrentAccount;
import com.study.inf.domain.Account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class studyController {

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model){

        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/form";

    }
    
}
