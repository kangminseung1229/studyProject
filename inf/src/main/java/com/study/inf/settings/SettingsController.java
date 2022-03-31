package com.study.inf.settings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.print.event.PrintJobAttributeEvent;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.inf.account.Account;
import com.study.inf.account.AccountService;
import com.study.inf.account.CurrentUser;
import com.study.inf.domain.Tag;
import com.study.inf.settings.forms.NicknameForm;
import com.study.inf.settings.forms.Notifications;
import com.study.inf.settings.forms.PasswordForm;
import com.study.inf.settings.forms.TagForm;
import com.study.inf.settings.validator.NicknameValidator;
import com.study.inf.settings.validator.PasswordFormValidator;
import com.study.inf.settings.validator.Profile;
import com.study.inf.tag.TagRepository;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/*
* 프로필 수정
*/

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/" + SETTINGS_NOTIFICATIONS_VIEW_NAME;
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;

    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;

    private final AccountService accountService;
    private final ModelMapper modelMapper; // controller 는 빈에 등록되어 있으니 주입받을 수 있고, new 생성자 클래스에서는 빈에 등록되어 있지 않기 때문에 주입 받을
                                           // 수 없다.
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        // model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class));

        return SETTINGS_PROFILE_VIEW_NAME;

    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다."); // redirect 시 사용, 한번쓰고 버려진다.

        return "redirect:" + SETTINGS_PROFILE_URL;

    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
            Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");

        return "redirect:" + SETTINGS_PASSWORD_URL;

    }

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        // model.addAttribute(new Notifications(account));
        model.addAttribute(modelMapper.map(account, Notifications.class));

        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
            Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }

        accountService.updateNotification(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/" + SETTINGS_NOTIFICATIONS_URL;

    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException{
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        

        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {

        // account 는 현재 datached
        // ManyToMany 의 기본 전략은 LAZY 기본전략
        // 하지만 LAZY 도 detached에선 가져올 수 없다.

        String title = tagForm.getTagTitle();

        // Tag tag = tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder()
        //         .title(tagForm.getTagTitle())
        //         .build()));

        Tag tag = tagRepository.findByTitle(title);
        if (tag == null){
        tag = tagRepository.save(Tag.builder().title(title).build());
        }

        accountService.addTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
            Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

}
