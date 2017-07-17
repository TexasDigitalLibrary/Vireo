package org.tdl.vireo.model;

import java.util.ArrayList;

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
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
    }

}
