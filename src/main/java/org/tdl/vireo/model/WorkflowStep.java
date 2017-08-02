package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.model.inheritance.HeratibleWorkflowStep;
import org.tdl.vireo.model.inheritance.HeritableComponent;
import org.tdl.vireo.model.validation.WorkflowStepValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
@SuppressWarnings("rawtypes")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }))
public class WorkflowStep extends AbstractWorkflowStep<WorkflowStep, FieldProfile, Note> implements HeratibleWorkflowStep {

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    protected Organization originatingOrganization;

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;

    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> originalFieldProfiles;

    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<Note> originalNotes;
    
    public WorkflowStep() {
        setModelValidator(new WorkflowStepValidator());
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
     * @return the originatingWorkflowStep
     */
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    /**
     * @param originatingWorkflowStep
     *            the originatingWorkflowStep to set
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
     * @param originatingOrganization
     *            the originatingOrganization to set
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
        if (!getOriginalFieldProfiles().contains(originalFieldProfile)) {
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
        for (FieldProfile fp : getOriginalFieldProfiles()) {
            if (fp.getId().equals(fp1.getId())) {
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
            if (fieldProfile.getFieldPredicate().equals(fieldPredicate))
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
        if (!getOriginalNotes().contains(originalNote)) {
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
        for (Note n : getOriginalNotes()) {
            if (n.getId().equals(n1.getId())) {
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

    @Override
    public void removeAggregateHeritableModel(HeritableComponent heritableModel) {
        if (heritableModel.getClass().equals(Note.class)) {
            removeAggregateNote((Note) heritableModel);
        }
        if (heritableModel.getClass().equals(FieldProfile.class)) {
            removeAggregateFieldProfile((FieldProfile) heritableModel);
        }
    }

    @Override
    public void addOriginalHeritableModel(HeritableComponent heritableModel) {
        if (heritableModel.getClass().equals(Note.class)) {
            addOriginalNote((Note) heritableModel);
        }
        if (heritableModel.getClass().equals(FieldProfile.class)) {
            addOriginalFieldProfile((FieldProfile) heritableModel);
        }
    }

    @Override
    public void addAggregateHeritableModel(HeritableComponent heritableModel) {
        if (heritableModel.getClass().equals(Note.class)) {
            addAggregateNote((Note) heritableModel);
        }
        if (heritableModel.getClass().equals(FieldProfile.class)) {
            addAggregateFieldProfile((FieldProfile) heritableModel);
        }
    }

    @Override
    public void removeOriginalHeritableModel(HeritableComponent heritableModel) {
        if (heritableModel.getClass().equals(Note.class)) {
            removeOriginalNote((Note) heritableModel);
        }
        if (heritableModel.getClass().equals(FieldProfile.class)) {
            removeOriginalFieldProfile((FieldProfile) heritableModel);
        }
    }

    @Override
    public List getOriginalHeritableModels(Class HeritableComponent) {
        List results = new ArrayList();
        if (HeritableComponent.equals(Note.class)) {
            results = getOriginalNotes();
        }
        if (HeritableComponent.equals(FieldProfile.class)) {
            results = getOriginalFieldProfiles();
        }
        return results;
    }

    @Override
    public List getAggregateHeritableModels(Class HeritableComponent) {
        List results = new ArrayList();
        if (HeritableComponent.equals(Note.class)) {
            results = getAggregateNotes();
        }
        if (HeritableComponent.equals(FieldProfile.class)) {
            results = getAggregateFieldProfiles();
        }
        return results;
    }

    @Override
    public boolean replaceAggregateHeritableModel(HeritableComponent newHeritableModel, HeritableComponent oldHeritableModel) {
        boolean results = false;
        if (newHeritableModel.getClass().equals(Note.class)) {
            results = replaceAggregateNote((Note) newHeritableModel, (Note) oldHeritableModel);
        }
        if (newHeritableModel.getClass().equals(FieldProfile.class)) {
            results = replaceAggregateFieldProfile((FieldProfile) newHeritableModel, (FieldProfile) oldHeritableModel);
        }
        return results;
    }

    @Override
    public WorkflowStep clone() {

        // not cloning id or originals

        WorkflowStep clone = new WorkflowStep();

        List<Note> aggregateNotes = new ArrayList<Note>();
        for (Note n : getAggregateNotes()) {
            aggregateNotes.add(n);
        }

        List<FieldProfile> aggregateFieldProfiles = new ArrayList<FieldProfile>();
        for (FieldProfile fp : getAggregateFieldProfiles()) {
            aggregateFieldProfiles.add(fp);
        }

        clone.setName(getName());
        clone.setOverrideable(getOverrideable());
        clone.setOriginatingOrganization(getOriginatingOrganization());
        clone.setOriginatingWorkflowStep(getOriginatingWorkflowStep());

        clone.setAggregateNotes(aggregateNotes);

        clone.setAggregateFieldProfiles(aggregateFieldProfiles);
        
        clone.setInstructions(this.getInstructions());

        return clone;
    }

}
