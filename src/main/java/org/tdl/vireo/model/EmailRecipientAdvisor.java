package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;

@Entity
public class EmailRecipientAdvisor extends AbstractEmailRecipient implements EmailRecipient {

    public EmailRecipientAdvisor() {
        setName("Advisor");
    }

    @Override
    public List<String> getEmails(Submission submission) {
        List<String> emails = new ArrayList<String>();
        Optional<FieldValue> optFv = submission.getFieldValuesByPredicateValue("dc.contributor.advisor").stream().findFirst();
        if(optFv.isPresent()){
            String email = null;
            Optional<String> optEmail = optFv.get().getContacts().stream().findFirst();
            if (optEmail.isPresent()) {
                email = optEmail.get();
                emails.add(email);
            }
        }
        return emails;
    }

}

