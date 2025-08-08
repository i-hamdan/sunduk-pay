package com.bxb.sunduk_pay.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
