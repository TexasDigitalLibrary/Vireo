package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

@EntityCV(name = "Degrees")
public interface DegreeRepo extends JpaRepository<Degree, Long>, EntityControlledVocabularyRepo<Degree>, DegreeRepoCustom {

    public Degree findByNameAndLevel(String name, DegreeLevel level);

    public Degree findByPosition(Long position);

    public List<Degree> findAllByOrderByPositionAsc();

}
