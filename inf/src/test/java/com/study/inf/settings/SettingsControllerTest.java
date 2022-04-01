package com.study.inf.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.inf.account.AccountRepository;
import com.study.inf.account.AccountService;
import com.study.inf.domain.Account;
import com.study.inf.domain.Tag;
import com.study.inf.settings.forms.TagForm;
import com.study.inf.tag.TagRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

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
        System.out.println("<=================전체삭제 실행=================>" );
        accountRepository.deleteAll();
    }

    

    @WithAccount("keesun")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception{
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                            .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                            .andExpect(model().attributeExists("account"))
                            .andExpect(model().attributeExists("whitelist"))
                            .andExpect(model().attributeExists("tags"));
    }

    
    @WithAccount("keesun")
    @DisplayName("계정의 태그 삭제")
    @Test
    void removeTag() throws Exception{

        Account keesun = accountRepository.findByNickname("keesun");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(keesun, newTag);

        assertTrue(keesun.getTags().contains(newTag));


        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL+"/remove")
                        .contentType(MediaType.APPLICATION_JSON) // 본문 타입이 json 으로 들어온다
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf())
        )
                        .andExpect(status().isOk());

        assertFalse(keesun.getTags().contains(newTag));

    }

    @WithAccount("keesun")
    @DisplayName("계정의 태그 추가")
    @Test
    void addTag() throws Exception{

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL+"/add")
                        .contentType(MediaType.APPLICATION_JSON) // 본문 타입이 json 으로 들어온다
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf())
        )
                        .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account keesun = accountRepository.findByNickname("keesun");
        assertTrue(keesun.getTags().contains(newTag));
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

    @WithAccount("keesun")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {

        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword", "1234545678")
            .param("newPasswordConfirm", "1234545678")
            .with(csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(flash().attributeExists("message"));

        
        Account keesun = accountRepository.findByNickname("keesun");
        assertTrue(passwordEncoder.matches("1234545678", keesun.getPassword()));
    }

    @WithAccount("keesun")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception{

        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword", "1234545678")
            .param("newPasswordConfirm", "123213123")
            .with(csrf())
        )
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("passwordForm"))
            .andExpect(model().attributeExists("account"));
        
    }

    @WithAccount("keesun")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception{
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeExists("account"))
                        .andExpect(model().attributeExists("nicknameForm"));
                        
    }

    @WithAccount("keesun")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception{
        String newNickname = "whiteship";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("nickname", newNickname)
                        .with(csrf())
        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                        .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("whiteship"));
    }
}
