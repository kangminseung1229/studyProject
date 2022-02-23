package com.study.inf.settings;

import com.study.inf.account.Account;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {


    @Length(max=50)
    private String bio;
    
    private String url;

    private String occupation;
    
    private String location;

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();

    }

    
    
}
