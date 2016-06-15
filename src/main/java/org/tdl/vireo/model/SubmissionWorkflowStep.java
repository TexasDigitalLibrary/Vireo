package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

public class SubmissionWorkflowStep extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;
    
    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<SubmissionFieldProfile> fieldProfiles;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<SubmissionNote> notes;
    
    public SubmissionWorkflowStep() {
        setFieldProfiles(new ArrayList<SubmissionFieldProfile>());
        setNotes(new ArrayList<SubmissionNote>());
    }
    
    public SubmissionWorkflowStep(String name) {
        this();
        setName(name);
    }
    
    public SubmissionWorkflowStep(String name, Organization originatingOrganization) {
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
     * 
     * @param param
     */
    public void setFieldProfiles(List<SubmissionFieldProfile> fieldProfiles) {
        this.fieldProfiles = fieldProfiles;
    }
    
    public void addFieldProfile(SubmissionFieldProfile fieldProfile)
    {
        getFieldProfiles().add(fieldProfile);
    }
    
    public List<SubmissionFieldProfile> getFieldProfiles()
    {
        return this.fieldProfiles;
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
    
    
    public List<SubmissionNote> getNotes() {
        return this.notes;
    }

    public void setNotes(List<SubmissionNote> notes) {
        this.notes = notes;
    }

    public void addNote(SubmissionNote note) {
        getNotes().add(note);
    }

    public void removeNote(SubmissionNote note) {
        getNotes().remove(note);
    }

    public void clearAllNotes() {
        getNotes().clear();
    }
    
}
