package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public interface FieldProfileRepo extends JpaRepository<FieldProfile, Long>, FieldProfileRepoCustom {

    public FieldProfile findByPredicate(FieldPredicate fieldPredicate);
    
    public void delete(FieldProfile fieldProfile);

}
