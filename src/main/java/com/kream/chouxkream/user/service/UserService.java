package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User loginProc(String email, String password) {

        User loginUser = userRepository.findByEmail(email);

//        System.out.println("암호화 password: " + bCryptPasswordEncoder.encode(password));

        // ToDo. email(아이디) 오류
        if (loginUser == null) {
//            System.out.println("login user null");
        }

        // ToDo. password 오류
//        System.out.println("loginUser = " + loginUser);

        return loginUser;
    }
}
