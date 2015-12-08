package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

public interface FieldGlossRepo extends JpaRepository<FieldGloss, Long>, FieldGlossRepoCustom {

    public List<FieldGloss> findByValue(String value);
    
    public FieldGloss findByValueAndLanguage(String value, Language language);

}
