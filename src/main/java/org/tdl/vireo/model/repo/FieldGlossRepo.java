package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface FieldGlossRepo extends WeaverRepo<FieldGloss>, FieldGlossRepoCustom {

    public List<FieldGloss> findByValue(String value);

    public FieldGloss findByValueAndLanguage(String value, Language language);

}
