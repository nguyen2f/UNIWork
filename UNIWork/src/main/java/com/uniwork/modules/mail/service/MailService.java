package com.uniwork.modules.mail.service;

import org.springframework.scheduling.annotation.Async;

public interface MailService {

    @Async
    void sendAssignMail(String toEmail, String projectName, String role);

    @Async
    void sendRegisterMail(String toEmail, String userName);

    @Async
    void sendNewTaskMail(String toEmail, String taskTitle, String taskDescription);
}
