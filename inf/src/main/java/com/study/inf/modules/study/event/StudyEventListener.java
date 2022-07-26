package com.study.inf.modules.study.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.inf.modules.study.Study;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class StudyEventListener {

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyCreatedEvent.getStudy();
        log.info(study.getTitle() + "is created.");
        // TODO 이메일을 보내거나, DB에 Notification 정보를 저장하면 된다.
        throw new RuntimeException();
    }

}