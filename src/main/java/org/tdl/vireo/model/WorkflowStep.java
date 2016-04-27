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

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
public class WorkflowStep extends BaseEntity {

    @Column(nullable = false)
    private String name;

    //TODO: determine necessity of this in light of the originatingWorkflowStep
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;
    
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, mappedBy = "workflowSteps", fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> containedByOrganizations;
    
    @Column(nullable = false)
    private Boolean optional;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private List<FieldProfile> fieldProfiles;
    
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
    
    public WorkflowStep(String name, Organization owningOrganization) {
        this(name, owningOrganization, owningOrganization);
    }
    
    public WorkflowStep(String name, Organization owningOrganization, Organization originatingOrganization) {
        this();
        setName(name);
        setOptional(true);
        addContainedByOrganization(owningOrganization);
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
     * @return the owningOrganization
     */
    public Set<Organization> getContainedByOrganizations() {
        return containedByOrganizations;
    }

    /**
     * @param owningOrganization the owningOrganization to set
     */
    public void setContainedByOrganizations(Set<Organization> owningOrganizations) {
        this.containedByOrganizations = owningOrganizations;
    }
    
    public void addContainedByOrganization(Organization owningOrganization) {
        this.containedByOrganizations.add(owningOrganization);
    }
    
    public void removeContainedByOrganization(Organization owningOrganization) {
        this.containedByOrganizations.remove(owningOrganization);
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
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
    
}
