package org.tdl.vireo.model.repo;

import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

@EntityCV(name = "Graduation Months")
public interface GraduationMonthRepo extends WeaverOrderedRepo<GraduationMonth>, EntityControlledVocabularyRepo<GraduationMonth>, GraduationMonthRepoCustom {

    public GraduationMonth findByMonth(int month);

}
