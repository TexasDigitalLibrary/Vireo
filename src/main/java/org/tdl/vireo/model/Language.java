package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.LanguageValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
public class Language extends BaseOrderedEntity {

    @Column(unique = true, nullable = false)
    private String name;

    public Language() {
        setModelValidator(new LanguageValidator());
    }

    /**
     *
     * @param name
     */
    public Language(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
