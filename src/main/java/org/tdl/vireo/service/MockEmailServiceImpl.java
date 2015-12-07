package org.tdl.vireo.service;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * The same as {@link EmailServiceImpl} but @Override doSend from {@link JavaMailSenderImpl}
 * 
 * @author gad
 *
 */
public class MockEmailServiceImpl extends EmailServiceImpl {
        
    public MockEmailServiceImpl() {
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        // DO NOT ACTUALLY SEND
    }
}
