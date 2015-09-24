package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

@Repository
public interface FieldProfileRepo extends JpaRepository<FieldProfile, Long>, FieldProfileRepoCustom {

	public FieldProfile create(FieldPredicate fieldPredicate, Boolean repeatable, Boolean required);
	
	public void delete(FieldProfile fieldProfile);
	
}
