package com.study.inf.account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

/*
* custom validator
*/
@Component
@RequiredArgsConstructor // private final 에 대하여 생성자를 만들어준다. 2.4.2 이후
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // TODO Auto-generated method stub
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        // TODO Auto-generated method stub 

        SignUpForm signupform = (SignUpForm) obj;
        if (accountRepository.existsByEmail(signupform.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[] { signupform.getEmail() }, "이미 사용중인 이메일 입니다.");

        }

        if (accountRepository.existsByNickname(signupform.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[] { signupform.getEmail() }, "이미 사용중인 닉네임 입니다.");

        }

    }

}
