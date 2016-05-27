package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
public class WorkflowStep extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean overrideable;
    
    @ManyToOne(cascade = { REFRESH }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    @ManyToOne(cascade = { REFRESH }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;

    @OneToMany(cascade = { REFRESH, REMOVE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> fieldProfiles;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "workflow_step_id", "fields_order", "fields_id" }))
    @OrderColumn
    private List<FieldProfile> fields;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<Note> notes;

    public WorkflowStep() {
    	setFields(new ArrayList<FieldProfile>());
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
    	addProfileToFields(fieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeFieldProfile(FieldProfile fieldProfile) {
    	getFieldProfiles().remove(fieldProfile);
    	removeProfileFromFields(fieldProfile);
    }
    
    /**
     * 
     * @return
     */
    public List<FieldProfile> getFields() {
        return fields;
    }

    /**
     * 
     * @param param
     */
    public void setFields(List<FieldProfile> fields) {
        this.fields = fields;
    }

    /**
     * 
     * @param aggregateFieldProfile
     */
    public void addProfileToFields(FieldProfile aggregateFieldProfile) {
        if(!getFields().contains(aggregateFieldProfile)) {
        	getFields().add(aggregateFieldProfile);
        }
    	
		// TODO: recurively add to aggregateFieldProfiles?
    }

    /**
     * 
     * @param aggregateFieldProfile
     */
    public void removeProfileFromFields(FieldProfile aggregateFieldProfile) {
    	getFields().remove(aggregateFieldProfile);
    	
    	// TODO: recurively remove from aggregateFieldProfiles?
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
    
    public boolean descendsFrom(WorkflowStep workflowStep) {
        if(getOriginatingWorkflowStep() == null) {
            return false;
        }
        else if(getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            return true;
        }
        else { 
            return getOriginatingWorkflowStep().descendsFrom(workflowStep);
        }
    }    


}
