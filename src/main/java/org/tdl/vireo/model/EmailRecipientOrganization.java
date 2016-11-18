package org.tdl.vireo.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EmailRecipientOrganization extends AbstractEmailRecipient implements EmailRecipient {
	
	@ManyToOne
	private Organization organization;

	public EmailRecipientOrganization() {}
	
	public EmailRecipientOrganization(Organization organization) {
		this.organization = organization;
	}

	@Override
	public List<String> getEmails(Submission submission) {
		return getOrganization().getEmails();
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
