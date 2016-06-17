package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;

import edu.tamu.framework.model.BaseEntity;
import edu.tamu.framework.model.ValidatingBase;

@MappedSuperclass
//@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name="WS_TYPE")
//@Table(name="ABSTRACT_WORKFLOW_STEP")
public abstract class AbstractWorkflowStep  <WS extends AbstractWorkflowStep<WS, FP, N>, 
                                             FP extends AbstractFieldProfile<FP>,
                                             N  extends AbstractNote<N>> 
                                             extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    //TODO:  can't constrain uniquely because we don't know the id column of the concrete class here
    @CollectionTable//(uniqueConstraints = @UniqueConstraint(columnNames = { "workflow_step_id", "aggregateFieldProfiles_order", "aggregate_field_profiles_id" }))
    @OrderColumn
    //@JoinColumn(name = "id", insertable = false, updatable = false)
    private List<FP> aggregateFieldProfiles;


    /**
     * @return the originatingOrganization
     */
    public abstract Organization getOriginatingOrganization();

    /**
     * @param originatingOrganization the originatingOrganization to set
     */
    public abstract void setOriginatingOrganization(Organization originatingOrganization);

    
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
    
    public void addFieldProfile(FP fieldProfile)
    {
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
    
    
    
}
