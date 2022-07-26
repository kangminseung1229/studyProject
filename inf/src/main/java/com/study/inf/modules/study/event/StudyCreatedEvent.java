package com.study.inf.modules.study.event;

import com.study.inf.modules.study.Study;

import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }

}
