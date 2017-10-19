package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.custom.CustomActionValueRepoCustom;

public class CustomActionValueRepoImpl implements CustomActionValueRepoCustom {

    @Autowired
    private SubmissionRepo submissionRepo;

    @Override
    public CustomActionValue create(Submission submission, CustomActionDefinition definition, Boolean value) {
        CustomActionValue cav = new CustomActionValue(definition, value);
        submission.addCustomActionValue(cav);
        submission = submissionRepo.save(submission);
        return submission.getCustomActionValue(cav);
    }

    @Override
    public void delete(CustomActionValue value) {
        Submission submission = submissionRepo.findByCustomActionValuesDefinitionLabel(value.getDefinition().getLabel());
        submission.removeCustomActionValue(value);
        submissionRepo.save(submission);
    }

}
