package com.study.inf.account;

import java.util.List;

import com.study.inf.domain.Account;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class UserAccount extends User{

    
    private Account account; // 이 지역변수의 이름과 principal 이름이 같아야한다.
    
    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
    
}
