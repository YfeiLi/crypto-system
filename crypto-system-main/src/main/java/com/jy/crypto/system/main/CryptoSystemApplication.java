package com.jy.crypto.system.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.jy.crypto.system")
@EnableCaching
@EnableScheduling
public class CryptoSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoSystemApplication.class, args);
        System.out.println("crypto-system started successfully");
    }
}
