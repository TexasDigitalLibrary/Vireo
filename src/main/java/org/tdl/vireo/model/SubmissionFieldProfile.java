package org.tdl.vireo.model;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Sub")
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "predicate_id", "inputType", "repeatable", "optional", "fp_type"}) )
public class SubmissionFieldProfile extends AbstractFieldProfile<SubmissionFieldProfile> {
        
    public SubmissionFieldProfile() {
        setOptional(true);
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
    } 

}
