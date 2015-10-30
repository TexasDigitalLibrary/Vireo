package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

public interface FieldPredicateRepo extends JpaRepository<FieldPredicate, Long>, FieldPredicateRepoCustom {

    public FieldPredicate findByValue(String value);

}
