package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;

public interface FieldProfileRepoCustom {

    public FieldProfile create(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean required);
}
