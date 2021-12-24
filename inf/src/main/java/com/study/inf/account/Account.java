package com.study.inf.account;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Account
 */

@Data
@Entity
@EqualsAndHashCode(of = "id")
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; 

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;
    
    private String url;

    private String occupation;
    
    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profielImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;
    
    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;
    
}