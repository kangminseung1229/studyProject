package com.study.inf.modules.settings.validator;



import com.study.inf.modules.settings.forms.PasswordForm;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator{

    @Override
    public boolean supports(Class<?> clazz) {
        // TODO Auto-generated method stub
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // TODO Auto-generated method stub

        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
            
        }
        
    }
    
}