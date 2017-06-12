package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;


@Entity
@JsonTypeName("EmailRecipientSubmitter")
public class EmailRecipientSubmitter extends AbstractEmailRecipient implements EmailRecipient {

    public EmailRecipientSubmitter() {
        setName("Submitter");
        setType("EmailRecipientSubmitter");
    }

    @Override
    public List<String> getEmails(Submission submission) {

        List<String> emails = new ArrayList<String>();

        emails.add(submission.getSubmitter().getSetting("preferedEmail"));

        return emails;

    }

}
