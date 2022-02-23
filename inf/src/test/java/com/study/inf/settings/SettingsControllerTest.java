package com.study.inf.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.study.inf.account.Account;
import com.study.inf.account.AccountRepository;
import com.study.inf.account.AccountService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;


    // bug로 안된다고 가정 시.
    // @BeforeEach // 테스트 메소드 1개 실행 전 실행 -> 1개당 1개
    // private void beforeEach() {
    //     SignUpForm signUpForm = new SignUpForm();
    //     signUpForm.setNickname("keesun");
    //     signUpForm.setEmail("keesun@email.com");
    //     signUpForm.setPassword("12345678");
    //     accountService.processNewAccount(signUpForm);
    //     System.out.println("======================");
    // }

    
    
    @AfterEach
    void AfterEach(){
        accountRepository.deleteAll();
    }
    

    // @WithUserDetails(value = "keesun", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithAccount("keesun")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception{

        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL)
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("keesun")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception{

        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));


            Account keesun = accountRepository.findByNickname("keesun");
            assertEquals(bio, keesun.getBio());

    }

    @WithAccount("keesun")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_wrong() throws Exception{

        String bio = "긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우긴 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

            Account keesun = accountRepository.findByNickname("keesun");
            assertNull(keesun.getBio());

    }
}
