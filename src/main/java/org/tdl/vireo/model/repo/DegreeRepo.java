package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

@EntityCV(name = "Degrees")
public interface DegreeRepo extends WeaverRepo<Degree>, EntityControlledVocabularyRepo<Degree>, DegreeRepoCustom {

    public Degree findByNameAndLevel(String name, DegreeLevel level);

    public Degree findByPosition(Long position);

    public List<Degree> findAllByOrderByPositionAsc();

}
