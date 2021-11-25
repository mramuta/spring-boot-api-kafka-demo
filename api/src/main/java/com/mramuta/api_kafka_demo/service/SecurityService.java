package com.mramuta.api_kafka_demo.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    public String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
