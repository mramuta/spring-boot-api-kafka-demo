package com.mramuta.api_kafka_demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityServiceTest {
    private SecurityService subject;

    @BeforeEach
    void setUp() {
        subject = new SecurityService();
    }

    @Test
    void shouldHashPassword() {
        String password = "somePassword";
        String hashedPassword = subject.hashPassword(password);

        assertThat(BCrypt.checkpw(password, hashedPassword)).isTrue();
    }
}