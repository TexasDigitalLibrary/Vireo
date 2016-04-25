package org.tdl.vireo.model;

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
    
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;
    
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, mappedBy="workflowSteps")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> owningOrganizations;
    
    @Column(nullable = false)
    private Boolean optional;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private List<FieldProfile> fieldProfiles;
    
    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private List<Note> notes;

    public WorkflowStep() {
        setFieldProfiles(new ArrayList<FieldProfile>());
        setNotes(new ArrayList<Note>());
        setOwningOrganizations(new TreeSet<Organization>());
    }
    
    public WorkflowStep(String name, Organization owningOrganization) {
        this(name, owningOrganization, owningOrganization);
    }
    
    public WorkflowStep(String name, Organization owningOrganization, Organization originatingOrganization) {
        this();
        setName(name);
        setOptional(true);
        addOwningOrganization(owningOrganization);
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
     * @return the owningOrganization
     */
    public Set<Organization> getOwningOrganizations() {
        return owningOrganizations;
    }

    /**
     * @param owningOrganization the owningOrganization to set
     */
    public void setOwningOrganizations(Set<Organization> owningOrganizations) {
        this.owningOrganizations = owningOrganizations;
    }
    
    public void addOwningOrganization(Organization owningOrganization) {
        this.owningOrganizations.add(owningOrganization);
    }
    
    public void removeOwningOrganization(Organization owningOrganization) {
        this.owningOrganizations.remove(owningOrganization);
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
        getFieldProfiles().add(fieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeFieldProfile(FieldProfile fieldProfile) {
        getFieldProfiles().remove(fieldProfile);
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
