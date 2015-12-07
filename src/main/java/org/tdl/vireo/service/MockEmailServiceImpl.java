package org.tdl.vireo.service;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * The same as {@link EmailServiceImpl} but @Override doSend from {@link JavaMailSenderImpl}
 * 
 * @author gad
 *
 */
@Service
public class MockEmailServiceImpl extends EmailServiceImpl {
        
    public MockEmailServiceImpl() {
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        // DO NOT ACTUALLY SEND
    }
}
