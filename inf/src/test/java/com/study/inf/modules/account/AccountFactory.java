package com.study.inf.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname){

        Account testUser = new Account();
        testUser.setNickname(nickname);
        testUser.setEmail(nickname+ "@email.com");
        accountRepository.save(testUser);

        return testUser;
    }
    
}
