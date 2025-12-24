package com.ja.easybookbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EasyBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyBookBackendApplication.class, args);
    }

}
