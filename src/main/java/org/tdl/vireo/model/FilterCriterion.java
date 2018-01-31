package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "value", "gloss" }) })
public class FilterCriterion extends BaseEntity {

    @Column
    private String value;

    @Column
    private String gloss;

    public FilterCriterion() {
        super();
    }

    public FilterCriterion(String value, String gloss) {
        this();
        setValue(value);
        setGloss(gloss);
    }

    public FilterCriterion(String value) {
        this(value, value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

}
