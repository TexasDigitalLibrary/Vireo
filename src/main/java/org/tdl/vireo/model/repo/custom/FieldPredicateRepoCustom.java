package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldPredicate;

public interface FieldPredicateRepoCustom {

	public FieldPredicate create(String value, Boolean documentTypePredicate);

}
