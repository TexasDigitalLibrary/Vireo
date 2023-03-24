package org.tdl.vireo.model.repo.simple;

import java.util.List;
import org.springframework.data.repository.Repository;
import org.tdl.vireo.model.simple.SimpleSubmission;

public interface SimpleSubmissionRepo extends Repository<SimpleSubmission, Long> {

    public List<SimpleSubmission> findAll();

    public List<SimpleSubmission> findAllBySubmitterId(Long submitterId);

    public SimpleSubmission findById(Long id);

    public SimpleSubmission findBySubmitterIdAndId(Long submitterId, Long id);

    public SimpleSubmission findByAdvisorAccessHash(String advisorAccessHash);
}
