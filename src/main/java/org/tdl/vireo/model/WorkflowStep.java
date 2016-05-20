package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
public class WorkflowStep extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean overrideable;
    
    // used when a workflow step is updated and needs a new workflow step
    @ManyToOne(cascade = { REFRESH }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    @ManyToOne(cascade = { REFRESH }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;

    // maybe needed to recursively add and remove aggregate workflow steps
//    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    private List<Organization> containingOrganizations;

    @OneToMany(cascade = { REFRESH, REMOVE }, orphanRemoval = true, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name="originating_organization_id")
    private List<FieldProfile> fieldProfiles;
    
    @OneToMany(cascade = { REFRESH }, fetch = EAGER)
    @OrderColumn
    private List<FieldProfile> aggregateFieldProfiles;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<Note> notes;

    public WorkflowStep() {
//    	setContainingOrganizations(new ArrayList<Organization>());
    	setAggregateFieldProfiles(new ArrayList<FieldProfile>());
        setFieldProfiles(new ArrayList<FieldProfile>());
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the originatingOrganization
     */
    public Organization getOriginatingOrganization() {
        return originatingOrganization;
    }

    /**
     * @param originatingOrganization the originatingOrganization to set
     */
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

    public Boolean getOverrideable() {
        return overrideable;
    }

    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }
    
//    public List<Organization> getContainingOrganizations() {
//        return containingOrganizations;
//    }
//
//    public void setContainingOrganizations(List<Organization> containingOrganizations) {
//        this.containingOrganizations = containingOrganizations;
//    }
//
//    public void addContainingOrganization(Organization containingOrganization) {
//    	getContainingOrganizations().add(containingOrganization);
//    }
//
//    public void removeContainingOrganization(Organization containingOrganization) {
//    	getContainingOrganizations().remove(containingOrganization);
//    }

    /**
     * 
     * @return
     */
    public List<FieldProfile> getFieldProfiles() {
        return fieldProfiles;
    }

    /**
     * 
     * @param param
     */
    public void setFieldProfiles(List<FieldProfile> fieldProfiles) {
        this.fieldProfiles = fieldProfiles;
    }

    /**
     * 
     * @param fieldProfile
     */
    public void addFieldProfile(FieldProfile fieldProfile) {
        if(!getFieldProfiles().contains(fieldProfile)) {
            getFieldProfiles().add(fieldProfile);
        }
    	addFieldProfileToAggregate(fieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeFieldProfile(FieldProfile fieldProfile) {
    	getFieldProfiles().remove(fieldProfile);
    	removeFieldProfileFromAggregate(fieldProfile);
    }
    
    /**
     * 
     * @return
     */
    public List<FieldProfile> getAggregateFieldProfiles() {
        return aggregateFieldProfiles;
    }

    /**
     * 
     * @param param
     */
    public void setAggregateFieldProfiles(List<FieldProfile> aggregateFieldProfiles) {
        this.aggregateFieldProfiles = aggregateFieldProfiles;
    }

    /**
     * 
     * @param fieldProfile
     */
    public void addFieldProfileToAggregate(FieldProfile aggregateFieldProfile) {
        if(!getAggregateFieldProfiles().contains(aggregateFieldProfile)) {
            getAggregateFieldProfiles().add(aggregateFieldProfile);
        }
    	
		// TODO: recurively add to aggregateFieldProfiles
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeFieldProfileFromAggregate(FieldProfile aggregateFieldProfile) {
    	getAggregateFieldProfiles().remove(aggregateFieldProfile);
    	
    	// TODO: recurively remove from aggregateFieldProfiles
    }

    /**
     * 
     * @param fieldPredicate
     * @return
     */
    public FieldProfile getFieldProfileByPredicate(FieldPredicate fieldPredicate) {
        for (FieldProfile fieldProfile : getFieldProfiles()) {
            if (fieldProfile.getPredicate().equals(fieldPredicate))
                return fieldProfile;
        }
        return null;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public void clearAllNotes() {
        notes.clear();
    }

}
