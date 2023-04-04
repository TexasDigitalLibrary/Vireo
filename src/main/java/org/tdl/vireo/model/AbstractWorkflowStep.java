package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;

import org.tdl.vireo.model.response.Views;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class AbstractWorkflowStep<WS extends AbstractWorkflowStep<WS, FP, N>, FP extends AbstractFieldProfile<FP>, N extends AbstractNote<N>> extends ValidatingBaseEntity {

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    private String name;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    private Boolean overrideable;

    @JsonView(Views.SubmissionIndividual.class)
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @OrderColumn
    private List<FP> aggregateFieldProfiles;

    @JsonView(Views.SubmissionIndividual.class)
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @OrderColumn
    private List<N> aggregateNotes;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(columnDefinition = "text")
    private String instructions;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the overrideable
     */
    public Boolean getOverrideable() {
        return overrideable;
    }

    /**
     * @param overrideable the overrideable to set
     */
    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }

    /**
     * @return the aggregateFieldProfiles
     */
    public List<FP> getAggregateFieldProfiles() {
        return aggregateFieldProfiles;
    }

    /**
     * @param aggregateFieldProfiles the aggregateFieldProfiles to set
     */
    public void setAggregateFieldProfiles(List<FP> aggregateFieldProfiles) {
        this.aggregateFieldProfiles = aggregateFieldProfiles;
    }
    public void addFieldProfile(FP fieldProfile) {
        getAggregateFieldProfiles().add(fieldProfile);
    }

    /**
     *
     * @param aggregateFieldProfile
     */
    public void addAggregateFieldProfile(FP aggregateFieldProfile) {
        if (!getAggregateFieldProfiles().contains(aggregateFieldProfile)) {
            getAggregateFieldProfiles().add(aggregateFieldProfile);
        }
    }

    /**
     *
     * @param aggregateFieldProfile
     */
    public void removeAggregateFieldProfile(FP aggregateFieldProfile) {
        getAggregateFieldProfiles().remove(aggregateFieldProfile);
    }

    /**
     * Replace the field profile.
     *
     * @param fp1 The FieldProfile to replace.
     * @param fp2 The FieldProfile to replace with.
     * @return True if replaced and false otherwise.
     */
    public boolean replaceAggregateFieldProfile(FP fp1, FP fp2) {
        boolean res = false;
        int pos = 0;
        for (FP fp : getAggregateFieldProfiles()) {
            if (fp.getId().equals(fp1.getId())) {
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
     * Swap the field profile.
     *
     * @param fp1 The FieldProfile to swap.
     * @param fp2 The FieldProfile to swap with.
     * @return True if swapped and false otherwise.
     */
    public boolean swapAggregateFieldProfile(FP fp1, FP fp2) {
        boolean res = false;

        int pos1 = getAggregateFieldProfiles().indexOf(fp1), pos2 = getAggregateFieldProfiles().indexOf(fp2);

        if (pos1 >= 0 && pos2 >= 0) {
            Collections.swap(getAggregateFieldProfiles(), pos1, pos2);
            res = true;
        }

        return res;
    }

    /**
     * Re-order field profiles by their index.
     *
     * @param src The index of the FieldProfile to swap from.
     * @param dest The index of the FieldProfile to swap to.
     */
    public void reorderAggregateFieldProfile(int src, int dest) {

        // adjust for index + 1
        src -= 1;
        dest -= 1;

        FP fieldProfile = getAggregateFieldProfiles().get(src);

        getAggregateFieldProfiles().remove(src);

        getAggregateFieldProfiles().add(dest, fieldProfile);
    }

    /**
     * @return the aggregateNotes
     */
    public List<N> getAggregateNotes() {
        return aggregateNotes;
    }

    /**
     * @param aggregateNotes the aggregateNotes to set
     */
    public void setAggregateNotes(List<N> aggregateNotes) {
        this.aggregateNotes = aggregateNotes;
    }

   /**
     * Append a note.
     *
     * @param aggregateNote the aggregateNote to append.
     */
    public void addAggregateNote(N aggregateNote) {
        if (!getAggregateNotes().contains(aggregateNote)) {
            getAggregateNotes().add(aggregateNote);
        }
    }

    /**
     * Remove a note.
     *
     * @param aggregateNote the aggregateNote to remove.
     */
    public void removeAggregateNote(N aggregateNote) {
        getAggregateNotes().remove(aggregateNote);
    }

    /**
     * Replace the note.
     *
     * @param n1 The AggregateNote to replace.
     * @param n2 The AggregateNote to replace with.
     * @return True if replaced and false otherwise.
     */
    public boolean replaceAggregateNote(N n1, N n2) {
        boolean res = false;
        int pos = 0;
        for (N n : getAggregateNotes()) {
            if (n.getId().equals(n1.getId())) {
                getAggregateNotes().remove(n1);
                getAggregateNotes().add(pos, n2);
                res = true;
                break;
            }
            pos++;
        }
        return res;
    }

    /**
     * Swap the note.
     *
     * @param n1 The AggregateNote to swap.
     * @param n2 The AggregateNote to swap with.
     * @return True if swapped and false otherwise.
     */
    public boolean swapAggregateNote(N n1, N n2) {
        boolean res = false;

        int pos1 = getAggregateNotes().indexOf(n1), pos2 = getAggregateNotes().indexOf(n2);

        if (pos1 >= 0 && pos2 >= 0) {
            Collections.swap(getAggregateNotes(), pos1, pos2);
            res = true;
        }

        return res;
    }

    /**
     * Re-order notes by their index.
     *
     * @param src The index of the AggregateNote to swap from.
     * @param dest The index of the AggregateNote to swap to.
     */
    public void reorderAggregateNote(int src, int dest) {

        // adjust for index + 1
        src -= 1;
        dest -= 1;

        N note = getAggregateNotes().get(src);

        getAggregateNotes().remove(src);

        getAggregateNotes().add(dest, note);
    }

    /**
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * @param instructions the instructions to set
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
