package com.study.inf.zone;

import com.study.inf.domain.Zone;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long>{
    Zone findByCityAndProvince(String cityName, String provinceName);    
}
