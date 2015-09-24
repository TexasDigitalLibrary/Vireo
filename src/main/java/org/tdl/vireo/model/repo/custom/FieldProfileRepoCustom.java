package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldPredicate;

public interface FieldProfileRepoCustom {

	public FieldProfile create(FieldPredicate fieldPredicate, Boolean repeatable, Boolean required);
	
	public void delete(FieldProfile fieldProfile);
	
}
