package com.study.inf.modules.tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long>{

    // Optional<Tag> findByTitle(String title);

    Tag findByTitle(String title);
    
}
