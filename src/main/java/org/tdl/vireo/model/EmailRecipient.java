package org.tdl.vireo.model;

import java.util.List;

public interface EmailRecipient {
	
	public List<String> getEmails(Submission submission);
	
}
