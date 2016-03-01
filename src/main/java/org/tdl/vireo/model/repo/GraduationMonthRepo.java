package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;

public interface GraduationMonthRepo extends JpaRepository<GraduationMonth, Long>, GraduationMonthRepoCustom {

}
