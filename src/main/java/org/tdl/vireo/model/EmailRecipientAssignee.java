package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class EmailRecipientAssignee extends AbstractEmailRecipient {

    public EmailRecipientAssignee() {
        setName("Assignee");
    }

    @Override
    public List<String> getEmails(Submission submission) {
        List<String> emails = new ArrayList<String>();
        User assignee = submission.getAssignee();
        if(assignee != null) {
          emails.add(assignee.getSetting("preferedEmail"));
        }
        return emails;
    }

}
