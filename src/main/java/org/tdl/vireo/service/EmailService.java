package org.tdl.vireo.service;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSender;

public interface EmailService extends JavaMailSender {
    public void sendEmail(String to, String subject, String text) throws MessagingException;
}
