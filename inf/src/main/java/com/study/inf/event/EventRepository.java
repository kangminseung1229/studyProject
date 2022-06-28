package com.study.inf.event;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.study.inf.domain.Event;
import com.study.inf.domain.Study;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event,Long> {


    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);

}
