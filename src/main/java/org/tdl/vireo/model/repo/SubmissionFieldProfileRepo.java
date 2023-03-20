package org.tdl.vireo.model.repo;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionFieldProfileRepo extends WeaverRepo<SubmissionFieldProfile>, JpaSpecificationExecutor<SubmissionFieldProfile>, SubmissionFieldProfileRepoCustom {

    public Optional<SubmissionFieldProfile> findOne(Specification<SubmissionFieldProfile> specification);

}
