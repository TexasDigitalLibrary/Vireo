package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.aspect.annotation.EntityControlledVocabulary;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;

@EntityControlledVocabulary(name = "Graduation Months")
public interface GraduationMonthRepo extends JpaRepository<GraduationMonth, Long>, EntityControlledVocabularyRepo<GraduationMonth>, GraduationMonthRepoCustom {

    public GraduationMonth findByMonth(int month);

    public GraduationMonth findByPosition(Long position);

    public List<GraduationMonth> findAllByOrderByPositionAsc();

}
