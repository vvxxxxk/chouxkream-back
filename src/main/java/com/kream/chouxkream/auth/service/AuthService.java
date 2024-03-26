package com.kream.chouxkream.auth.service;

import com.kream.chouxkream.auth.model.entity.RefreshToken;
import com.kream.chouxkream.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository jwtRepository;

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
