package com.himedia.spserver;

import com.himedia.spserver.security.CustomSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SpringBootServerApplicationTests {

    @Autowired
    CustomSecurityConfig cc;


    @Test
    void contextLoads() {
        PasswordEncoder pe = cc.passwordEncoder();
        System.out.println(pe.encode("1234"));
    }



}
