package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EmailRecipientContact extends AbstractEmailRecipient implements EmailRecipient {
	
	@ManyToOne
	private FieldPredicate fieldPredicate;

	public EmailRecipientContact() {}
	
	public EmailRecipientContact(FieldPredicate fieldPredicate) {
		this.fieldPredicate = fieldPredicate;
	}

	@Override
	public List<String> getEmails(Submission submission) {
		
		List<String> emails = new ArrayList<String>();
		
		for(FieldValue fv : submission.getFieldValueByPredicate(getFieldPredicate())) {
			emails.add(fv.getValue());
		};
		
		return emails;
		
	}

	public FieldPredicate getFieldPredicate() {
		return fieldPredicate;
	}

	public void setFieldPredicate(FieldPredicate fieldPredicate) {
		this.fieldPredicate = fieldPredicate;
	}

}

