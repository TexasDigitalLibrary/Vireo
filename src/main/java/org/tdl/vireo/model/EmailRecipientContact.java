package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EmailRecipientContact extends AbstractEmailRecipient implements EmailRecipient {

    @ManyToOne
    private FieldPredicate fieldPredicate;

    public EmailRecipientContact() {
    }

    public EmailRecipientContact(String label, FieldPredicate fieldPredicate) {
        setName(label);
        this.fieldPredicate = fieldPredicate;
    }

    @Override
    public List<String> getEmails(Submission submission) {

        List<String> emails = new ArrayList<String>();

        for (FieldValue fv : submission.getFieldValuesByPredicate(getFieldPredicate())) {
            System.out.println("Looking at field value " + fv.getValue() + "(" + fv.getId()
                    + ") gotten from submission " + submission.getId() + "'s predicates matching "
                    + getFieldPredicate().getValue() + "(" + getFieldPredicate().getId());
            for (String contact : fv.getContacts()) {
                System.out.println("That field value has a contact value of " + contact);
                if (!emails.contains(contact))
                    emails.add(contact);
            }
        }

        return emails;

    }

    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

}
