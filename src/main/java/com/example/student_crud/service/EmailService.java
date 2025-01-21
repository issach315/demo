package com.example.student_crud.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to Our Student Portal");
        message.setText("Dear " + name + ",\n\nThank you for registering with our student portal. Your registration has been successful.\n\nBest regards,\nStudent Admin Team");
        mailSender.send(message);
    }
}

