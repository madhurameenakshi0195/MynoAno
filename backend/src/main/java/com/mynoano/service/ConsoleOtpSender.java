package com.mynoano.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Slf4j
@Component
@Profile("dev")
public class ConsoleOtpSender implements OtpSender {
    @Override
    public void send(String email, String phone, String code) {
        log.info("DEV OTP for {} / {}: {}", email, phone, code);
    }
}
