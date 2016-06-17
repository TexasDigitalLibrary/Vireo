package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.InputType;

import edu.tamu.framework.model.BaseEntity;

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
