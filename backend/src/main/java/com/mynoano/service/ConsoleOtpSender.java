package com.mynoano.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleOtpSender implements OtpSender {
    @Override
    public void send(String email, String phone, String code) {
        log.info("DEV OTP for {} / {}: {}", email, phone, code);
    }
}
