package com.study.inf.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.study.inf.domain.Event;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event,Long> {



}
