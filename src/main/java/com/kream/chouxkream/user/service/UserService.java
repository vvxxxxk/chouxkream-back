package com.kream.chouxkream.user.service;

import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.repository.RoleRepository;
import com.kream.chouxkream.user.exception.UserServiceExeption;
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
    public void join(UserJoinDto userJoinDto) {

        ResponseMessage responseMessage;

        // 이메일 인증 여부 체크
        if (!userJoinDto.isEmailAuth()) {
            responseMessage = new ResponseMessage(400, "이메일 인증은 필수입니다.", null);
            throw new UserServiceExeption(responseMessage);
        }

        // 이메일 중복 체크
        if (userRepository.findByEmail(userJoinDto.getEmail()).isPresent()) {
            responseMessage = new ResponseMessage(400, "중복된 이메일 입니다.", null);
            throw new UserServiceExeption(responseMessage);
        }

        User user = userJoinDto.toEntity();
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);

        // 권한 체크
        Optional<Role> optionalRole = roleRepository.findByRoleName("ROLE_USER");
        if (optionalRole.isEmpty()) {
            responseMessage = new ResponseMessage(400, "존재하지 않는 권한입니다.", null);
            throw new UserServiceExeption(responseMessage);
        }
        Role role = optionalRole.get();
        UserRoleKey userRoleKey = new UserRoleKey(user.getUserNo(), role.getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(userRoleKey);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    @Async
    public void sendAuthEmail(String email) {

        ResponseMessage responseMessage;

        String authNum = makeRandomNumber();
        String setFrom = "shhwang0930@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
        String content =
                "나의 APP을 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNum + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요"; //이메일 내용 삽입

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류 이러한 경우 MessagingException이 발생

            responseMessage = new ResponseMessage(400, "이메일 주소, 인증번호를 다시 확인해주세요", null);
            throw new UserServiceExeption(responseMessage);
        }

        AuthNumber authNumber = new AuthNumber(email, authNum);
        authNumberRepositroy.save(authNumber);
    }

    //임의의 6자리 양수를 반환합니다.
    public String makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++)
            randomNumber += Integer.toString(r.nextInt(10));

        return randomNumber;
    }

    public boolean checkAuthNumber(String email, String authNum) {

        ResponseMessage responseMessage;
        Optional<AuthNumber> optionalAuthNumber = authNumberRepositroy.findById(email);

        if (optionalAuthNumber.isPresent()) {

            AuthNumber authNumber = optionalAuthNumber.get();

            if (email.equals(authNumber.getEmail()) && authNum.equals(authNumber.getAuthNum())) {

                return true;
            } else {

                responseMessage = new ResponseMessage(400, "이메일 주소, 인증번호를 다시 확인해주세요", null);
                throw new UserServiceExeption(responseMessage);
            }

        } else {
            responseMessage = new ResponseMessage(400, "이메일 주소, 인증번호를 다시 확인해주세요", null);
            throw new UserServiceExeption(responseMessage);
        }
    }

    @Transactional
    public String findEmailByPhoneNumber(String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            return emailMasking(optionalUser.get().getEmail());
        } else {

            ResponseMessage responseMessage = new ResponseMessage(404, "일치하는 회원 정보를 찾을 수 없습니다.", null);
            throw new UserServiceExeption(responseMessage);
        }
    }

    private String emailMasking(String email) {

        // 이메일에서 아이디 추출
        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹

        return id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);
    }

    @Transactional
    public User getUserInfo(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        return null;
    }
}
