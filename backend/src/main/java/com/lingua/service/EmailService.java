package com.lingua.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@linguaai.com");
            message.setTo(toEmail);
            message.setSubject("Welcome to LinguaAI!");
            message.setText("Hi " + (name != null ? name : "User") + ",\n\n" +
                    "Welcome to LinguaAI! We are super excited to have you on board.\n" +
                    "Get ready to master a new language with the help of your AI Tutor.\n\n" +
                    "Best regards,\nThe LinguaAI Team");
            emailSender.send(message);
            System.out.println("Welcome email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to: " + toEmail);
            e.printStackTrace();
        }
    }
}
