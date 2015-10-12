package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;

public interface CustomActionValueRepoCustom {
	public CustomActionValue create(Submission submission,CustomActionDefinition definition,Boolean value);
}
