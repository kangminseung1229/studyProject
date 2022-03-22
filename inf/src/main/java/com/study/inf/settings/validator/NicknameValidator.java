package com.study.inf.settings.validator;

import static org.mockito.Mockito.ignoreStubs;

import com.study.inf.account.Account;
import com.study.inf.account.AccountRepository;
import com.study.inf.settings.forms.NicknameForm;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator{

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // TODO Auto-generated method stub
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // TODO Auto-generated method stub
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());

        if(byNickname != null){
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
        
    }

    
}
