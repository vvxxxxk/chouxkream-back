package com.kream.chouxkream.user.service;

import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.repository.RoleRepository;
import com.kream.chouxkream.user.model.dto.UserJoinDto;
import com.kream.chouxkream.user.model.dto.UserRoleKey;
import com.kream.chouxkream.user.model.entity.AuthNumber;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.AuthNumberRepositroy;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthNumberRepositroy authNumberRepositroy;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;

    @Transactional
    public boolean isEmailExists(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    @Transactional
    public void join(UserJoinDto userJoinDto) {

        // 회원 저장
        User user = userJoinDto.toEntity();
        user.setActive(true);
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);

        // 일반 회원 권한 체크
        Optional<Role> optionalRole = roleRepository.findByRoleName("ROLE_USER");
        Role role = optionalRole.get();

        // 회원-권한 저장
        UserRoleKey userRoleKey = new UserRoleKey(user.getUserNo(), role.getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(userRoleKey);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    @Async
    public void sendAuthEmail(String email) throws MessagingException {

        String authNum = makeAuthNumber();
        String setFrom = "shhwang0930@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[CHOUXKREAM]회원가입 인증 이메일";
        String content =
                "CHOUXKREAM을 방문해주셔서 감사합니다." +
                "<br><br>" +
                "인증 번호는 " + authNum + "입니다." +
                "<br>" +
                "인증번호를 제대로 입력해주세요";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
        // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
        helper.setFrom(setFrom);    //이메일의 발신자 주소 설정
        helper.setTo(toMail);       //이메일의 수신자 주소 설정
        helper.setSubject(title);   //이메일의 제목을 설정
        helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
        mailSender.send(message);

        AuthNumber authNumber = new AuthNumber(email, authNum);
        authNumberRepositroy.save(authNumber);
    }

    public String makeAuthNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++)
            randomNumber += Integer.toString(r.nextInt(10));

        return randomNumber;
    }

    public boolean checkAuthNumber(String email, String authNum) {

        Optional<AuthNumber> optionalAuthNumber = authNumberRepositroy.findById(email);
        if (optionalAuthNumber.isPresent()) {

            AuthNumber authNumber = optionalAuthNumber.get();

            if (email.equals(authNumber.getEmail()) && authNum.equals(authNumber.getAuthNum())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Transactional
    public Optional<User> findByPhoneNumber(String phoneNumber) {

        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Transactional
    public void updateEmail(String email, String updateEmail) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setEmail(updateEmail);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateName(String email, String updateName) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setUsername(updateName);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateNickname(String email, String updateNickname) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setNickname(updateNickname);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUserDesc(String email, String updateUserDesc) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setUserDesc(updateUserDesc);
            userRepository.save(user);
        }
    }

    @Transactional
    public void sendTempPasswordEmail(String email) throws MessagingException {

        String tempPassword = generateTempPassword();
        String setFrom = "shhwang0930@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[CHOUXKREAM]임시 비밀번호 발급 이메일";
        String content =
                "임시 발급된 패스워드 입니다. " +
                        "<br><br>" +
                        "인증 번호는 " + tempPassword + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
        // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
        helper.setFrom(setFrom);//이메일의 발신자 주소 설정
        helper.setTo(toMail);//이메일의 수신자 주소 설정
        helper.setSubject(title);//이메일의 제목을 설정
        helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
        mailSender.send(message);

        User user = userRepository.findByEmail(email).get();
        user.setPassword(tempPassword);
        user.encodePassword(bCryptPasswordEncoder);
        System.out.println("tempPassword = " + tempPassword);

        userRepository.save(user);
    }

    public String generateTempPassword() {

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String special_chars = "@$!%*#?&";
        String all_chars = upper + lower + digits + special_chars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 최소 8자 이상, 최대 16자 이하의 임시 비밀번호 생성
        int length = random.nextInt(9) + 8;

        // 적어도 하나의 영문자, 숫자, 특수문자 를 추가
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special_chars.charAt(random.nextInt(special_chars.length())));
        // 나머지 문자 추가
        for (int i = 0; i < length - 3; i++) {
            password.append(all_chars.charAt(random.nextInt(all_chars.length())));
        }

        // 임시 비밀번호를 무작위로 섞음
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(length);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }

        return password.toString();
    }

    @Transactional
    public boolean isPasswordCheck(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    @Transactional
    public void updatePassword(String email, String updatePassword) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setPassword(updatePassword);
            user.encodePassword(bCryptPasswordEncoder);
            userRepository.save(user);
        }
    }

    public int getMyPoints(String email) {
        User targetUser = userRepository.findByEmail(email).get();
        return targetUser.getPoint();
    }

    public void DeActivateUser(String email) {
        User targetUser = userRepository.findByEmail(email).get();
        targetUser.deActivate();

    }
}
