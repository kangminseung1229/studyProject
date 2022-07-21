package com.study.inf.modules.settings.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;

import lombok.Data;

@Data
public class NicknameForm {

    @NotBlank
    @Length(min = 3, max =20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")//ㄱ부터 ㅎ 까지, 가부터 힣 까지 _언더바 - 하이푼 {3글자에서 20글자}
    private String nickname;
    
}
