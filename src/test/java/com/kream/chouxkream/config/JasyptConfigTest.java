package com.kream.chouxkream.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/*
class JasyptConfigTest {

    @Test
    public void stringEncryptor() {

        String encrptKey = System.getProperty("jasypt.encryptor.password");
        String plainText = "test";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(encrptKey);

        String encryptedText = jasypt.encrypt(plainText);
        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println("encryptedText = " + encryptedText);
        System.out.println("decryptedText = " + decryptedText);

        Assertions.assertThat(plainText).isEqualTo(decryptedText);
    }

}*/