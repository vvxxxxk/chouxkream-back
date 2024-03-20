package com.kream.chouxkream.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JasyptConfigTest {

    @Test
    public void stringEncryptor() {

        String encrptKey = System.getProperty("jasypt.encryptor.password");
        String plainText = "jdbc:mysql://localhost:3306/jwt_study?UTC&characterEncoding=UTF-8";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(encrptKey);

        String encryptedText = jasypt.encrypt(plainText);
        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println("encryptedText = " + encryptedText);
        System.out.println("decryptedText = " + decryptedText);

        assertThat(plainText).isEqualTo(decryptedText);
    }

}