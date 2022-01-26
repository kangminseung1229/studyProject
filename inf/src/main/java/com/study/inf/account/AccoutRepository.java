package com.study.inf.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface AccoutRepository extends JpaRepository<Account, Long>{

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String string);


    

}
