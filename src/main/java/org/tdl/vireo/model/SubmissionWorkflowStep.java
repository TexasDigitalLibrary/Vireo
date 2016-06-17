package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originatingOrganizationId" }) )
public class SubmissionWorkflowStep extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
//    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
    // used only to provide a unique key combination for this entity
    //
    @Column(nullable = false)
    private Long originatingOrganizationId;
    
    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
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
        setOriginatingOrganizationId(originatingOrganization.getId());
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
     * @return the originatingOrganizationId
     */
    public Long getOriginatingOrganizationId() {
        return originatingOrganizationId;
    }

    /**
     * @param originatingOrganizationId the originatingOrganizationId to set
     */
    public void setOriginatingOrganizationId(Long originatingOrganizationId) {
        this.originatingOrganizationId = originatingOrganizationId;
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
