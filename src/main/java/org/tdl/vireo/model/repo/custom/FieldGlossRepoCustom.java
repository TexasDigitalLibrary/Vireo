package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldGloss;

public interface FieldGlossRepoCustom {

	public FieldGloss create(String value);
	
	public FieldGloss update(FieldGloss gloss);
	
	public void delete(FieldGloss gloss);
	
}
