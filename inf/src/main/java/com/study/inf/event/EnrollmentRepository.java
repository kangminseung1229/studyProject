package com.study.inf.event;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.inf.domain.Account;
import com.study.inf.domain.Enrollment;
import com.study.inf.domain.Event;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
    
}
