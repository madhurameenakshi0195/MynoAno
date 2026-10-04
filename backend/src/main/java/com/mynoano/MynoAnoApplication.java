package com.mynoano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MynoAnoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MynoAnoApplication.class, args);
    }
}
