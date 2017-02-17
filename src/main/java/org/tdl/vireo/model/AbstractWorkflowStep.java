package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;

import edu.tamu.framework.model.BaseEntity;

@MappedSuperclass
public abstract class AbstractWorkflowStep <WS extends AbstractWorkflowStep<WS, FP, N>,
                                            FP extends AbstractFieldProfile<FP>,
                                            N  extends AbstractNote<N>>
                                            extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean overrideable;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @OrderColumn
    private List<FP> aggregateFieldProfiles;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @OrderColumn
    private List<N> aggregateNotes;

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
     *
     * @return
     */
    public List<FP> getAggregateFieldProfiles() {
        return aggregateFieldProfiles;
    }

    /**
     *
     * @param param
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
        if(!getAggregateFieldProfiles().contains(aggregateFieldProfile)) {
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
     *
     * @param fp1
     * @param fp2
     * @return
     */
    public boolean replaceAggregateFieldProfile(FP fp1, FP fp2) {
        boolean res = false;
        int pos = 0;
        for(FP fp : getAggregateFieldProfiles()) {
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
    public boolean swapAggregateFieldProfile(FP fp1, FP fp2) {
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

        FP fieldProfile = getAggregateFieldProfiles().get(src);

        getAggregateFieldProfiles().remove(src);

        getAggregateFieldProfiles().add(dest, fieldProfile);
    }

    public List<N> getAggregateNotes() {
        return aggregateNotes;
    }

    public void setAggregateNotes(List<N> aggregateNotes) {
        this.aggregateNotes = aggregateNotes;
    }

    public void addAggregateNote(N aggregateNote) {
        if(!getAggregateNotes().contains(aggregateNote)) {
            getAggregateNotes().add(aggregateNote);
        }
    }

    public void removeAggregateNote(N aggregateNote) {
        getAggregateNotes().remove(aggregateNote);
    }

    public boolean replaceAggregateNote(N n1, N n2) {
        boolean res = false;
        int pos = 0;
        for(N n : getAggregateNotes()) {
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

    public boolean swapAggregateNote(N n1, N n2) {
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

        N note = getAggregateNotes().get(src);

        getAggregateNotes().remove(src);

        getAggregateNotes().add(dest, note);
    }

}
