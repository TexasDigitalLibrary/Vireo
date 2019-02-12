package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class EmailRecipientPlainAddress extends AbstractEmailRecipient implements EmailRecipient {

    public EmailRecipientPlainAddress() {}
    
    public EmailRecipientPlainAddress(String address) {
        setName(address);
    }

    @Override
    public List<String> getEmails(Submission submission) {
        List<String> emails = new ArrayList<String>();
        emails.add(getName());
        return emails;
    }

}
