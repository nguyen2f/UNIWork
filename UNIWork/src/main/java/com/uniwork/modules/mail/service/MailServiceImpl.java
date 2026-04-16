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
        message.setSubject("Bạn đã được thêm vào dự án");
        message.setText(
                "Xin chào,\n\n" +
                        "Bạn đã được assign vào dự án: " + projectName + "\n" +
                        "Vai trò: " + seperateRoleString(role) + "\n\n" +
                        "Vui lòng đăng nhập hệ thống để xem chi tiết: https://v0-uniwork.vercel.app/"
        );

        mailSender.send(message);
    }

    @Override
    public void sendRegisterMail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Chào mừng bạn đến với UNIWork!");
        message.setText("Xin chào " + userName + ",\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản tại UNIWork. Chúng tôi rất vui được chào đón bạn đến với cộng đồng của chúng tôi.\n\n" +
                "Truy cập vào hệ thống để khám phá các tính năng và bắt đầu làm việc cùng chúng tôi: https://v0-uniwork.vercel.app/\n\n" +
                "Nếu bạn có bất kỳ câu hỏi hoặc cần hỗ trợ, đừng ngần ngại liên hệ với chúng tôi.\n\n" +
                "Chúc bạn một ngày làm việc hiệu quả!\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ UNIWork"
        );
    }

    private String seperateRoleString(String role) {
        if (role.contains("_")) {
            return role.replace("_", " ");
        }
        return role;
    }
}
