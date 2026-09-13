package com.example.habittt.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     */
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Habittt - 验证码");

        // 邮件内容
        String content = String.format(
                "Hiii！\n" +
                        "这里是Habittt~\n" +
                        "验证码：%s\n" +
                        "验证码有效期为5分钟ෆ\n" +
                        "如果这不是您的操作，请忽略此邮件\n" +
                        "ς(>‿<.)\n",
                code
        );

        message.setText(content);
        mailSender.send(message);
    }
}
