package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
@DiscriminatorValue("Sub")
public class SubmissionWorkflowStep extends AbstractWorkflowStep<SubmissionWorkflowStep, SubmissionFieldProfile, SubmissionNote> {

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    protected Organization originatingOrganization;
    
    public SubmissionWorkflowStep() {
        setAggregateFieldProfiles(new ArrayList<SubmissionFieldProfile>());
        setAggregateNotes(new ArrayList<SubmissionNote>());
    }
    
    public SubmissionWorkflowStep(String name) {
        this();
        setName(name);
        setOverrideable(true);
    }
    
    public SubmissionWorkflowStep(String name, Organization originatingOrganization) {
        this(name);
        setOriginatingOrganization(originatingOrganization);
    }
    
    
    /**
     * @return the originatingOrganization
     */
    @Override
    public Organization getOriginatingOrganization() {
        return originatingOrganization;
    }

    /**
     * @param originatingOrganization the originatingOrganization to set
     */
    @Override
    public void setOriginatingOrganization(Organization originatingOrganization) {
        this.originatingOrganization = originatingOrganization;
    }
       
}
