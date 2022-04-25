package com.study.inf.study;

import com.study.inf.domain.Study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>{

    boolean existsByPath(String path);

    Study findByPath(String path);

    

}
