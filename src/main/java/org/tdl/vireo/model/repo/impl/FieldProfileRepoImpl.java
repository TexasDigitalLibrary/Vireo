package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FieldProfileRepo fieldProfileRepo;
	
	@Override
	public FieldProfile create(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean required) {
		return fieldProfileRepo.save(new FieldProfile(fieldPredicate, inputType, repeatable, required));
	}
	
	@Override
	@Transactional
	public void delete(FieldProfile fieldProfile) {
		entityManager.remove(entityManager.contains(fieldProfile) ? fieldProfile : entityManager.merge(fieldProfile));
	}
	
}
