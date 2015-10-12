package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.custom.CustomActionValueRepoCustom;

public class CustomActionValueRepoImpl implements CustomActionValueRepoCustom {
	@Autowired
	CustomActionValueRepo customActionValueRepo;
	

	@Override
	public CustomActionValue create(Submission submission, CustomActionDefinition definition, Boolean value) {
		
		return customActionValueRepo.save(new CustomActionValue(submission, definition, value));
	}

}
