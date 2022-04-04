package com.study.inf.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Zone
 */

 @Entity
 @Data
 @EqualsAndHashCode(of="id")
 @Builder @AllArgsConstructor @NoArgsConstructor
public class Zone {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String localNameOfCity;
    
    @Column(nullable = true)
    private String province;



    
}