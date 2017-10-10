package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.LanguageValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
public class Language extends BaseOrderedEntity implements EntityControlledVocabulary {

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

    @Override
    public String getControlledName() {
        return name;
    }

    @Override
    public String getControlledDefinition() {
        return "";
    }

    @Override
    public String getControlledIdentifier() {
        return "";
    }

    @Override
    public List<String> getControlledContacts() {
        return new ArrayList<String>();
    }

}
