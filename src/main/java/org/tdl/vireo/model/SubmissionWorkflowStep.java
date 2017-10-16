package org.tdl.vireo.model;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.SubmissionWorkflowStepValidator;

@Entity
@DiscriminatorValue("Sub")
public class SubmissionWorkflowStep extends AbstractWorkflowStep<SubmissionWorkflowStep, SubmissionFieldProfile, SubmissionNote> {

    public SubmissionWorkflowStep() {
        setModelValidator(new SubmissionWorkflowStepValidator());
        setOverrideable(true);
        setAggregateFieldProfiles(new ArrayList<SubmissionFieldProfile>());
        setAggregateNotes(new ArrayList<SubmissionNote>());
    }

    public SubmissionWorkflowStep(String name) {
        this();
        setName(name);
    }

}
