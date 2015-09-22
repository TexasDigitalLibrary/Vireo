package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.ControlledVocabulary;

public interface ControlledVocabularyRepoCustom {

	public ControlledVocabulary create(String name);
	
	public ControlledVocabulary update(ControlledVocabulary controlledVocabulary);
	
	public void delete(ControlledVocabulary controlledVocabulary);
	
}
