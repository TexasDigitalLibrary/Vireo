package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

public interface DegreeRepo extends JpaRepository<Degree, Long>, EntityControlledVocabularyRepo<Degree>, DegreeRepoCustom {

    public Degree findByNameAndProquestCode(String name, String proquestCode);

    public Degree findByPosition(Long position);

    public List<Degree> findAllByOrderByPositionAsc();

}
