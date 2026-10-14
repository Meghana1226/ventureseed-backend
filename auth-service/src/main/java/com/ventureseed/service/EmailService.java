package com.ventureseed.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    // =========================================================
    // EMAIL VERIFICATION
    // =========================================================

    public void sendVerificationEmail(
            String toEmail,
            String verificationToken) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("ventureseedteam@gmail.com");

        message.setTo(toEmail);

        message.setSubject(
                "VentureSeed - Email Verification Code"
        );

        message.setText(
                "Hello,\n\n"
                + "Thank you for registering with VentureSeed.\n\n"
                + "Your email verification code is:\n\n"
                + verificationToken
                + "\n\n"
                + "This code is valid for 10 minutes.\n\n"
                + "Please enter this code on the VentureSeed "
                + "verification page.\n\n"
                + "If you did not create this account, "
                + "you can ignore this email.\n\n"
                + "Regards,\n"
                + "VentureSeed Team"
        );

        mailSender.send(message);
    }


    // =========================================================
    // PASSWORD RESET
    // =========================================================

    public void sendPasswordResetEmail(
            String toEmail,
            String resetToken) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("ventureseedteam@gmail.com");

        message.setTo(toEmail);

        message.setSubject(
                "VentureSeed - Password Reset Code"
        );

        message.setText(
                "Hello,\n\n"
                + "We received a request to reset your "
                + "VentureSeed password.\n\n"
                + "Your password reset code is:\n\n"
                + resetToken
                + "\n\n"
                + "This code is valid for 10 minutes.\n\n"
                + "Enter this code on the VentureSeed "
                + "password reset page and create a new password.\n\n"
                + "If you did not request a password reset, "
                + "you can safely ignore this email.\n\n"
                + "Regards,\n"
                + "VentureSeed Team"
        );

        mailSender.send(message);
    }
}