package org.tdl.vireo.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.SubmissionFieldProfileValidator;

@Entity
@DiscriminatorValue("Sub")
public class SubmissionFieldProfile extends AbstractFieldProfile<SubmissionFieldProfile> {

    public SubmissionFieldProfile() {
        setModelValidator(new SubmissionFieldProfileValidator());
        setOptional(true);
        setHidden(false);
        setFlagged(false);
        setLogged(false);
        setEnabled(true);
    }

}
