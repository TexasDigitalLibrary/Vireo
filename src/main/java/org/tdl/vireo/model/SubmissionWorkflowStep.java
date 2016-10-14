package org.tdl.vireo.model;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.SubmissionWorkflowStepValidator;

@Entity
@DiscriminatorValue("Sub")
public class SubmissionWorkflowStep extends AbstractWorkflowStep<SubmissionWorkflowStep, SubmissionFieldProfile, SubmissionNote> {

    public SubmissionWorkflowStep() {
        setOverrideable(true);
        setModelValidator(new SubmissionWorkflowStepValidator());
        setAggregateFieldProfiles(new ArrayList<SubmissionFieldProfile>());
        setAggregateNotes(new ArrayList<SubmissionNote>());
    }

    public SubmissionWorkflowStep(String name) {
        this();
        setName(name);
    }

}
