package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Override
	public ControlledVocabulary create(String name) {
		ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
		if(controlledVocabulary == null) {
			return controlledVocabularyRepo.save(new ControlledVocabulary(name));
		}
		return controlledVocabulary;
	}
	
}
