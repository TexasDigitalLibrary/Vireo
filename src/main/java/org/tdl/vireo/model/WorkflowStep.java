package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collections;
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
    
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization originatingOrganization;

    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> originalFieldProfiles;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "workflow_step_id", "aggregateFieldProfiles_order", "aggregate_field_profiles_id" }))
    @OrderColumn
    private List<FieldProfile> aggregateFieldProfiles;
    
    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<Note> originalNotes;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "workflow_step_id", "aggregateNotes_order", "aggregate_notes_id" }))
    @OrderColumn
    private List<Note> aggregateNotes;

    public WorkflowStep() {
    	setAggregateFieldProfiles(new ArrayList<FieldProfile>());
        setOriginalFieldProfiles(new ArrayList<FieldProfile>());
        setAggregateNotes(new ArrayList<Note>());
        setOriginalNotes(new ArrayList<Note>());
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
     * @param aggregateFieldProfile
     */
    public void addAggregateFieldProfile(FieldProfile aggregateFieldProfile) {
        if(!getAggregateFieldProfiles().contains(aggregateFieldProfile)) {
        	getAggregateFieldProfiles().add(aggregateFieldProfile);
        }
    }

    /**
     * 
     * @param aggregateFieldProfile
     */
    public void removeAggregateFieldProfile(FieldProfile aggregateFieldProfile) {
    	getAggregateFieldProfiles().remove(aggregateFieldProfile);
    }
    
    /**
     * 
     * @param fp1
     * @param fp2
     * @return
     */
    public boolean replaceAggregateFieldProfile(FieldProfile fp1, FieldProfile fp2) {    	
    	boolean res = false;
    	int pos = 0;
    	for(FieldProfile fp : getAggregateFieldProfiles()) {
    		if(fp.getId().equals(fp1.getId())) {
    			getAggregateFieldProfiles().remove(fp1);
    			getAggregateFieldProfiles().add(pos, fp2);
    			res = true;
    			break;
    		}
    		pos++;
    	}
    	return res;
    }
    
    /**
     * 
     * @param fp1
     * @param fp2
     * @return
     */
    public boolean swapAggregateFieldProfile(FieldProfile fp1, FieldProfile fp2) {
        boolean res = false;
        
        int pos1 = getAggregateFieldProfiles().indexOf(fp1), 
            pos2 = getAggregateFieldProfiles().indexOf(fp2);
       
        if(pos1 >= 0 && pos2 >= 0) {
            Collections.swap(getAggregateFieldProfiles(), pos1, pos2);
            res = true;
        }
        
        return res;
    }
    
    /**
     * 
     * @param fp1
     * @param fp2
     * @return
     */
    public void reorderAggregateFieldProfile(int src, int dest) {
        
        //adjust for index + 1
        src -= 1;
        dest -= 1;
        
        FieldProfile fieldProfile = getAggregateFieldProfiles().get(src);
                
        getAggregateFieldProfiles().remove(src);
                
        getAggregateFieldProfiles().add(dest, fieldProfile);
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

    
    public List<Note> getOriginalNotes() {
        return originalNotes;
    }

    public void setOriginalNotes(List<Note> originalNotes) {
        this.originalNotes = originalNotes;
    }

    public void addOriginalNote(Note originalNote) {
        if(!getOriginalNotes().contains(originalNote)) {
            getOriginalNotes().add(originalNote);
        }
        addAggregateNote(originalNote);
    }

    public void removeOriginalNote(Note originalNote) {
        getOriginalNotes().remove(originalNote);
        removeAggregateNote(originalNote);
    }
   
    public boolean replaceOriginalNote(Note n1, Note n2) {
        boolean res = false;
        int pos = 0;
        for(Note n : getOriginalNotes()) {
            if(n.getId().equals(n1.getId())) {
                getOriginalNotes().remove(n1);
                getOriginalNotes().add(pos, n2);
                res = true;
                break;
            }
            pos++;
        }
        replaceAggregateNote(n1, n2);
        return res;
    }
    
    public List<Note> getAggregateNotes() {
        return aggregateNotes;
    }

    public void setAggregateNotes(List<Note> aggregateNotes) {
        this.aggregateNotes = aggregateNotes;
    }

    public void addAggregateNote(Note aggregateNote) {
        if(!getAggregateNotes().contains(aggregateNote)) {
            getAggregateNotes().add(aggregateNote);
        }
    }

    public void removeAggregateNote(Note aggregateNote) {
        getAggregateNotes().remove(aggregateNote);
    }
    
    public boolean replaceAggregateNote(Note n1, Note n2) {       
        boolean res = false;
        int pos = 0;
        for(Note n : getAggregateNotes()) {
            if(n.getId().equals(n1.getId())) {
                getAggregateNotes().remove(n1);
                getAggregateNotes().add(pos, n2);
                res = true;
                break;
            }
            pos++;
        }
        return res;
    }
    
    public boolean swapAggregateNote(Note n1, Note n2) {
        boolean res = false;
        
        int pos1 = getAggregateNotes().indexOf(n1), 
            pos2 = getAggregateNotes().indexOf(n2);
       
        if(pos1 >= 0 && pos2 >= 0) {
            Collections.swap(getAggregateNotes(), pos1, pos2);
            res = true;
        }
        
        return res;
    }
    
    public void reorderAggregateNote(int src, int dest) {
        
        //adjust for index + 1
        src -= 1;
        dest -= 1;
        
        Note note = getAggregateNotes().get(src);
                
        getAggregateNotes().remove(src);
                
        getAggregateNotes().add(dest, note);
    }
    
}
