package com.berk.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String email, String otp) throws MessagingException {
        // E-posta gönderme işlemleri burada gerçekleştirilecek
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String subject = "Doğrulama Kodu";
        String text = "Merhaba, doğrulama kodunuz: " + otp;

        helper.setText(text, true);
        helper.setTo(email);
        helper.setSubject(subject);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new MailSendException(e.getMessage()); // E-posta gönderme hatası
        }

    }
}
