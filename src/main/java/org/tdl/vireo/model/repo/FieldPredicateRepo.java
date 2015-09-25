package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

@Repository
public interface FieldPredicateRepo extends JpaRepository<FieldPredicate, Long>, FieldPredicateRepoCustom {

	public FieldPredicate create(String value);
	
}
