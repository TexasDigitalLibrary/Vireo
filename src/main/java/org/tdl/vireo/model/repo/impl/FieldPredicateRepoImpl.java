package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

public class FieldPredicateRepoImpl implements FieldPredicateRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FieldPredicateRepo fieldPredicateRepo;
	
	@Override
	public FieldPredicate create(String value) {
		return fieldPredicateRepo.save(new FieldPredicate(value));
	}
	
	@Override
	@Transactional
	public void delete(FieldPredicate fieldPredicate) {
		entityManager.remove(entityManager.contains(fieldPredicate) ? fieldPredicate : entityManager.merge(fieldPredicate));
	}
	
}
