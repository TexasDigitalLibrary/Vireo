package org.tdl.vireo.model;

import javax.persistence.Embeddable;

@Embeddable
public class FilterCriterion {

    private String value;
    private String gloss;

    public FilterCriterion() {}

    public FilterCriterion(String value, String gloss) {
        setValue(value);
        setGloss(gloss);
    }

    public FilterCriterion(String value) {
        setValue(value);
        setGloss(value);
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
