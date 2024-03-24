package com.kream.chouxkream.jwt.service;

import com.kream.chouxkream.jwt.model.entity.RefreshToken;
import com.kream.chouxkream.jwt.repository.JwtRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtService {

    private final JwtRepository jwtRepository;

    public JwtService(JwtRepository jwtRepository) {
        this.jwtRepository = jwtRepository;
    }

    @Transactional
    public void saveRefreshToken(String refreshToken, String email) {
        jwtRepository.save(new RefreshToken(refreshToken, email));
    }

    @Transactional
    public Boolean isExistRefreshToken(String refreshToken) {
        return jwtRepository.existsById(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        jwtRepository.deleteById(refreshToken);
    }
}
