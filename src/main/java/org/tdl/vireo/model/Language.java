package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.LanguageValidator;

import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;

@Entity
public class Language extends ValidatingOrderedBaseEntity implements EntityControlledVocabulary {

    @Column(unique = true, nullable = false)
    private String name;

    public Language() {
        setModelValidator(new LanguageValidator());
    }

    /**
     * Initializer.
     *
     * @param name
     */
    public Language(String name) {
        this.name = name;
    }

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
