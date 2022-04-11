package com.study.inf.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.study.inf.account.form.SignUpForm;
import com.study.inf.domain.Account;
import com.study.inf.domain.Tag;
import com.study.inf.domain.Zone;
import com.study.inf.settings.forms.Notifications;
import com.study.inf.settings.validator.Profile;

import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    // private final ConsoleMailSender javaMailSender;
    private final ModelMapper modelMapper;

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText(
                "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
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

    public void updateNotification(Account account, Notifications notifications) {

        modelMapper.map(notifications, account);

        // account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
        // account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
        // account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
        // account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
        // account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
        // account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());

        accountRepository.save(account);

    }

    public void updateNickname(Account account, String nickname) {

        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디올래, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
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
