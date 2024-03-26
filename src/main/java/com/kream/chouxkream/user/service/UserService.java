package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String findEmailProcess(String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            String maskingEmail = emailMasking(optionalUser.get().getEmail());
            return maskingEmail;
        }

        return null;
    }

    private String emailMasking(String email) {

        // 이메일에서 아이디 추출
        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹
        String maskingResult = id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);

        return maskingResult;
    }
}
