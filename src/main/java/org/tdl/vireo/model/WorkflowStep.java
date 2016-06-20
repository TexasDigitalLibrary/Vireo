package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
//@Table(name="WORKFLOW_STEP")
@DiscriminatorValue("Org")
public class WorkflowStep extends AbstractWorkflowStep<WorkflowStep, FieldProfile, Note> {

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    protected Organization originatingOrganization;
   
    // TODO: refactor with correct spelling, remember the getter and setters as well
    @Column(nullable = false)
    private Boolean overrideable;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    

    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> originalFieldProfiles;
    
    
    
    public WorkflowStep() {
    	setAggregateFieldProfiles(new ArrayList<FieldProfile>());
        setOriginalFieldProfiles(new ArrayList<FieldProfile>());
        setNotes(new ArrayList<Note>());
    }

    public WorkflowStep(String name) {
        this();
        setName(name);
        setOverrideable(true);
    }
    
    public WorkflowStep(String name, Organization originatingOrganization) {
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
    
    /**
     * @return the originatingWorkflowStep
     */
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    /**
     * @param originatingWorkflowStep the originatingWorkflowStep to set
     */
    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }

    /**
     * 
     * @return
     */
    public Boolean getOverrideable() {
        return overrideable;
    }

    /**
     * 
     * @param overrideable
     */
    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }

    /**
     * 
     * @return
     */
    public List<FieldProfile> getOriginalFieldProfiles() {
        return originalFieldProfiles;
    }

    /**
     * 
     * @param param
     */
    public void setOriginalFieldProfiles(List<FieldProfile> originalFieldProfiles) {
        this.originalFieldProfiles = originalFieldProfiles;
    }

    /**
     * 
     * @param fieldProfile
     */
    public void addOriginalFieldProfile(FieldProfile originalFieldProfile) {
        if(!getOriginalFieldProfiles().contains(originalFieldProfile)) {
            getOriginalFieldProfiles().add(originalFieldProfile);
        }
    	addAggregateFieldProfile(originalFieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeOriginalFieldProfile(FieldProfile originalFieldProfile) {
    	getOriginalFieldProfiles().remove(originalFieldProfile);
    	removeAggregateFieldProfile(originalFieldProfile);
    }
    
    /**
     * 
     * @param fp1
     * @param fp2
     * @return
     */
    public boolean replaceOriginalFieldProfile(FieldProfile fp1, FieldProfile fp2) {
        boolean res = false;
        int pos = 0;
        for(FieldProfile fp : getOriginalFieldProfiles()) {
            if(fp.getId().equals(fp1.getId())) {
                getOriginalFieldProfiles().remove(fp1);
                getOriginalFieldProfiles().add(pos, fp2);
                res = true;
                break;
            }
            pos++;
        }
        replaceAggregateFieldProfile(fp1, fp2);
        return res;
    }
    
    

    
    /**
     * 
     * @param fieldPredicate
     * @return
     */
    public FieldProfile getFieldProfileByPredicate(FieldPredicate fieldPredicate) {
        for (FieldProfile fieldProfile : getOriginalFieldProfiles()) {
            if (fieldProfile.getPredicate().equals(fieldPredicate))
                return fieldProfile;
        }
        return null;
    }

    
    
}
