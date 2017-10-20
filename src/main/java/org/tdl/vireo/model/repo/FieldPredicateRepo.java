package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface FieldPredicateRepo extends WeaverRepo<FieldPredicate>, FieldPredicateRepoCustom {

    public FieldPredicate findByValue(String value);

}
