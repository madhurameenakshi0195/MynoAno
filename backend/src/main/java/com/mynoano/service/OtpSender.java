package com.mynoano.service;

/** Swap the implementation for a real SMS / email provider (Twilio, SES, MSG91...). */
public interface OtpSender {
    void send(String email, String phone, String code);
}
