package com.uniwork.service.impl;

import com.uniwork.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendAssignMail(String toEmail, String projectName, String role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bạn đã được thêm vào dự án");
        message.setText(
                "Xin chào,\n\n" +
                        "Bạn đã được assign vào dự án: " + projectName + "\n" +
                        "Vai trò: " + seperateRoleString(role) + "\n\n" +
                        "Vui lòng đăng nhập hệ thống để xem chi tiết: https://v0-uniwork.vercel.app/"
        );

        mailSender.send(message);
    }
    private String seperateRoleString(String role) {
        if (role.contains("_")) {
            return role.replace("_", " ");
        }
        return role;
    }
}
