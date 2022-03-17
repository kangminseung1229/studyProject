package com.study.inf.settings;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class Profile {

    @Length(max = 50)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation; 

    @Length(max = 50)
    private String location;

    private String profileImage;

    
}
