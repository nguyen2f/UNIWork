package com.uniwork.service;

import org.springframework.scheduling.annotation.Async;

public interface MailService {

    @Async
    void sendAssignMail(String toEmail, String projectName, String role);
}
