package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class EmailRecipientAssignee extends AbstractEmailRecipient implements EmailRecipient {

    public EmailRecipientAssignee() {
        setName("Assignee");
    }

    @Override
    public List<String> getEmails(Submission submission) {
        List<String> emails = new ArrayList<String>();
        User adssignee = submission.getAssignee();
        if(adssignee != null) {
          emails.add(adssignee.getSetting("preferedEmail"));
        }
        return emails;
    }

}
