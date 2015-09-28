package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;

public interface FieldValueRepoCustom {

    public FieldValue create(FieldPredicate fieldPredicate);
}
