package com.master_thesis.verifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VerifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerifierApplication.class, args);
    }

    private PublicParameters publicParameters;

}