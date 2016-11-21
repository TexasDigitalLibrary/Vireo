package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;

public interface AbstractEmailRecipientRepoCustom {
	
	public EmailRecipient createOrganizationRecipient(Organization organization);

	EmailRecipient createAssigneeRecipient();

	EmailRecipient createSubmitterRecipient();

	EmailRecipient createContactRecipient(String label, FieldPredicate fieldPredicate);

}
