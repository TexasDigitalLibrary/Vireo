package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

public class FieldGlossRepoImpl implements FieldGlossRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FieldGlossRepo fieldGlossRepo;
	
	@Override
	public FieldGloss create(String value) {
		FieldGloss fieldGloss = fieldGlossRepo.findByValue(value);
		if(fieldGloss == null) {
			return fieldGlossRepo.save(new FieldGloss(value));
		}
		return fieldGloss;
	}
	
}
