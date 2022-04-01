package com.study.inf.account;

import com.study.inf.domain.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>{

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String string);

    Account findByNickname(String emailOrNickname);

}
