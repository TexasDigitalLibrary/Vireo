package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

public class FieldValueRepoImpl implements FieldValueRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FieldValueRepo fieldValueRepo;
	
	@Override
	public FieldValue create(FieldPredicate fieldPredicate) {
		return fieldValueRepo.save(new FieldValue(fieldPredicate));
	}
	
	@Override
	@Transactional
	public void delete(FieldValue fieldValue) {
		entityManager.remove(entityManager.contains(fieldValue) ? fieldValue : entityManager.merge(fieldValue));
	}
	
}
