package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldValue;

public interface FieldValueRepoCustom {

	public FieldValue create(FieldProfile fieldProfile);
	
	public void delete(FieldValue fieldValue);
	
}
