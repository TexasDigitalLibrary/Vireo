package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ControlledVocabulary extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private Language language;

    @ElementCollection
    @Column(columnDefinition = "TEXT", nullable = true, unique = true)
    private Set<String> values;

    public ControlledVocabulary() {
        setValues(new HashSet<String>());
    }

    /**
     * 
     * @param name
     * @param language
     */
    public ControlledVocabulary(String name, Language language) {
        this();
        setName(name);
        setLanguage(language);
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
     * @return the values
     */
    public Set<String> getValues() {
        return values;
    }

    /**
     * 
     * @return Language language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * 
     * @param language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @param values
     *            the values to set
     */
    public void setValues(Set<String> values) {
        this.values = values;
    }

    /**
     * 
     * @param value
     */
    public void addValue(String value) {
        getValues().add(value);
    }

    /**
     * 
     * @param value
     */
    public void removeValue(String value) {
        getValues().remove(value);
    }

    public String getValueByValue(String value) {
        for (String v : getValues()) {
            if (v.equals(value))
                return v;
        }
        return null;
    }
}
