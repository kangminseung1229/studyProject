package com.study.inf.account;

import static org.mockito.Mockito.lenient;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    // 커스텀한 validator 를 적용한다.
    @InitBinder("signUpForm")
    public void InitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) { // 객체에서 valid 어노테이션을 해야한다.

        // valid로 조건을 정의하고 조건에 부합하지 않을 시 Errors 객체에 담긴다.
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
            Account account =  accountService.processNewAccount(signUpForm);
            accountService.login(account);
        return "redirect:/";

    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {

        Account account = accountRepository.findByEmail(email);

        String view = "account/checked-email";
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return  view;
        }


        //토큰값이 일치하지 않으면
        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        
        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
            return  view;
    }

}
