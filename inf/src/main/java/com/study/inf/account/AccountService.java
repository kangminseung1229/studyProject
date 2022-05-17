package com.study.inf.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.study.inf.account.form.SignUpForm;
import com.study.inf.config.AppProperties;
import com.study.inf.domain.Account;
import com.study.inf.domain.Tag;
import com.study.inf.domain.Zone;
import com.study.inf.mail.ConsoleEmailService;
import com.study.inf.mail.EmailMessage;
import com.study.inf.mail.EmailService;
import com.study.inf.settings.forms.Notifications;
import com.study.inf.settings.validator.Profile;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendSignUpConfirmEmail(Account newAccount) {


        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail() );
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        
        
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
            .to(newAccount.getEmail())
            .subject("스터디올래, 회원 가입 인증")
            .message(message)
            .build();

            emailService.sendEmail(emailMessage);

    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();

        return accountRepository.save(account);
    }

    

    public Account processNewAccount(@Valid SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
                account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true) // 읽기전용 트랜잭션 - 성능에 유리
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);

        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname); // 두번 검사 후에도 에러가날 경우 예외를 던진다.
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp(); // 영속성 컨텍스트에 의해 taransaction에 넣어주어야 한다. Transaction 은 service에서 위임한다.
        login(account);

    }

    public void updateProfile(Account account, @Valid Profile profile) {

        modelMapper.map(profile, account); // 아래 대체

        // account.setUrl(profile.getUrl());
        // account.setOccupation(profile.getOccupation());
        // account.setLocation(profile.getLocation());
        // account.setBio(profile.getBio());
        // account.setProfileImage(profile.getProfileImage());

        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {

        modelMapper.map(notifications, account);

        // account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
        // account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
        // account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
        // account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
        // account.setStudyEnrollmentReSUltByWeb(notifications.isStudyEnrollmentResultByWeb());
        // account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());

        accountRepository.save(account);

    }

    public void updateNickname(Account account, String nickname) {

        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "이메일 로그인하기");
        context.setVariable("message", "로그인하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);



        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디올래, 로그인 링크")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
        // getOne -> Lazy
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));

    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }

}
