package org.tdl.vireo.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
public class EmailRecipientOrganization extends AbstractEmailRecipient implements EmailRecipient {
		
	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
	private Organization organization;

	public EmailRecipientOrganization() {}
	
	public EmailRecipientOrganization(Organization organization) {
		setName(organization.getName());
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
