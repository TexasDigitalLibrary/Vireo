package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Override
	public ControlledVocabulary create(String name) {
		return controlledVocabularyRepo.save(new ControlledVocabulary(name));
	}
	
//	@Override
//	public ControlledVocabulary update(ControlledVocabulary controlledVocabulary) {
//		return controlledVocabularyRepo.update(controlledVocabulary);
//	}
//	
//	@Override
//	public void delete(ControlledVocabulary controlledVocabulary) {
//		controlledVocabularyRepo.delete(controlledVocabulary);
//	}
	
}
