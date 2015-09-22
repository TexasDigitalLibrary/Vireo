package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

@Repository
public interface FieldGlossRepo extends JpaRepository<FieldGloss, Long>, FieldGlossRepoCustom {

	public FieldGloss create(String value);
	
//	public FieldGloss update(FieldGloss gloss);
//	
//	public void delete(FieldGloss gloss);
	
}
