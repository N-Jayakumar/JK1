package com.jk1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public boolean sendEmail(String to, String subject, String content) {
        log.info("================================================");
        log.info("Sending Email to: {}", to);
        log.info("Subject: {}", subject);
        log.info("Content: {}", content);
        log.info("================================================");
        // In a real scenario, use JavaMailSender here.
        // Return true to simulate successful delivery
        return true;
    }
}
