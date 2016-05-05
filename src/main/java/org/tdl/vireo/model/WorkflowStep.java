package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    //the organization for which this workflow step was created
    //need to know if a given organization is the owner
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;

    //the workflow step from which this one is derived
    //need to know so we can tell if things are overrideable by this workflow step
    //use null if this workflow step is original
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    //the organizations that use this workflow step
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, mappedBy = "workflowSteps", fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> containedByOrganizations;
    

    @Column(nullable = false)
    private Boolean overrideable;

    //the field profiles used in this workflow step
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<FieldProfile> fieldProfiles;
    
    //the field profiles that originated in this workflow step
    //need to know so that when a field profile is overridden we know if it is derivative
    //need to know so that when the workflow step is deleted, the field profiles originating in it are deleted, not orphaned
    @OneToMany(cascade = ALL, mappedBy = "originatingWorkflowStep", orphanRemoval = true, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<FieldProfile> originalFieldProfiles;
    
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private List<Note> notes;

    public WorkflowStep() {
        setOriginalFieldProfiles(new ArrayList<FieldProfile>());
        setFieldProfiles(new ArrayList<FieldProfile>());
        setNotes(new ArrayList<Note>());
        setContainedByOrganizations(new TreeSet<Organization>());
    }
    
    public WorkflowStep(String name, Organization originatingOrganization) {
        this(name, originatingOrganization, originatingOrganization);
    }
    
    public WorkflowStep(String name, Organization containingOrganization, Organization originatingOrganization) {
        this();
        setName(name);
        setOverrideable(true);
        addContainedByOrganization(containingOrganization);
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
    
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }

    /**
     * @return the Organizations that use (contain) this workflow step
     */
    public Set<Organization> getContainedByOrganizations() {
        return containedByOrganizations;
    }

    /**
     * @param containingOrganizations the list of Organizations that use (contain) this WorkflowStep to set
     */
    public void setContainedByOrganizations(Set<Organization> containingOrganizations) {
        this.containedByOrganizations = containingOrganizations;
    }
    
    public void addContainedByOrganization(Organization containingOrganization) {
        this.containedByOrganizations.add(containingOrganization);
    }
    
    public void removeContainedByOrganization(Organization containingOrganization) {
        this.containedByOrganizations.remove(containingOrganization);
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
        this.fieldProfiles.add(fieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeFieldProfile(FieldProfile fieldProfile) {
        this.fieldProfiles.remove(fieldProfile);
    }
    
    /**
     * @return the originalFieldProfiles
     */
    public List<FieldProfile> getOriginalFieldProfiles() {
        return originalFieldProfiles;
    }

    /**
     * @param originalFieldProfiles the originalFieldProfiles to set
     */
    public void setOriginalFieldProfiles(List<FieldProfile> originalFieldProfiles) {
        this.originalFieldProfiles = originalFieldProfiles;
    }
    
    /**
     * 
     * @param fieldProfile
     */
    public void addOriginalFieldProfile(FieldProfile originalFieldProfile) {
        this.originalFieldProfiles.add(originalFieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeOriginalFieldProfile(FieldProfile originalFieldProfile) {
        this.originalFieldProfiles.remove(originalFieldProfile);
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
    
    public boolean descendsFrom(WorkflowStep putativeAncestor)
    {
        if(originatingWorkflowStep == null)
        {
            return false;
        }
        else if(originatingWorkflowStep.equals(putativeAncestor))
        {
            return true;
        }
        else
        {
            return this.getOriginatingWorkflowStep().descendsFrom(putativeAncestor);
        }
    }
    
}
