package com.uniwork.modules.mail.service;

import com.uniwork.modules.mail.service.MailService;
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
        message.setSubject("You have been added to a project");
        message.setText(
                "Hello,\n\n" +
                        "You have been assigned to the project: " + projectName + "\n" +
                        "Role: " + seperateRoleString(role) + "\n\n" +
                        "Please log in to the system to view details: https://v0-uniwork.vercel.app/"
        );

        mailSender.send(message);
    }

    @Override
    public void sendRegisterMail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to UNIWork!");
        message.setText("Hello " + userName + ",\n\n" +
                "Thank you for registering an account with UNIWork. We are excited to have you in our community.\n\n" +
                "Access the system to explore features and start working with us: https://v0-uniwork.vercel.app/\n\n" +
                "If you have any questions or need support, feel free to contact us.\n\n" +
                "Have a productive day!\n\n" +
                "Best regards,\n" +
                "UNIWork Team"
        );
    }

    @Override
    @Async
    public void sendNewTaskMail(String toEmail, String taskTitle, String taskDescription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("You have a new task: " + taskTitle);
        message.setText("Hello,\n\n" +
                "You have been assigned a new task with the following details:\n\n" +
                "Title: " + taskTitle + "\n" +
                "Description: " + taskDescription + "\n\n" +
                "Please log in to the system to view details: https://v0-uniwork.vercel.app/\n\n" +
                "Best regards,\n" +
                "UNIWork Team"
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