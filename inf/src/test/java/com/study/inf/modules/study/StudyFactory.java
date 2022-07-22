package com.study.inf.modules.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.study.inf.modules.account.Account;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    @Autowired StudyService studyService;
    // private final StudyRepository studyRepository;

    public Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }




    
}
