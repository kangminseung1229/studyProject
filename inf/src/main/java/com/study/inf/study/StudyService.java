package com.study.inf.study;

import javax.websocket.server.ServerEndpoint;

import com.study.inf.domain.Account;
import com.study.inf.domain.Study;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    public Study createNewStudy(Study study, Account account) {

        Study newStudy = studyRepository.save(study);

        study.addManager(account);
        return null;
    }



}
