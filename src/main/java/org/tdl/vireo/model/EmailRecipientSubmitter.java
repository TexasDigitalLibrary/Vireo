package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class EmailRecipientSubmitter extends AbstractEmailRecipient implements EmailRecipient {

	public EmailRecipientSubmitter() {}

	@Override
	public List<String> getEmails(Submission submission) {
		
		List<String> emails = new ArrayList<String>();
		
		emails.add(submission.getSubmitter().getEmail());
		
		return emails;
		
	}

}


