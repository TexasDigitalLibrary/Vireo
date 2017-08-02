package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.VocabularyWordValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "controlled_vocabulary_id" }))
public class VocabularyWord extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String definition;

    @Column(nullable = true)
    private String identifier;

    @ElementCollection(fetch = EAGER)
    private List<String> contacts;

    @ManyToOne(cascade = { DETACH, REFRESH }, fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = ControlledVocabulary.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private ControlledVocabulary controlledVocabulary;

    public VocabularyWord() {
        setModelValidator(new VocabularyWordValidator());
    }

    public VocabularyWord(String name) {
        this();
        setName(name);
    }

    public VocabularyWord(String name, String definition) {
        this(name);
        setDefinition(definition);
    }

    public VocabularyWord(String name, String definition, String identifier) {
        this(name, definition);
        setIdentifier(identifier);
    }

    public VocabularyWord(String name, String definition, String identifier, List<String> contacts) {
        this(name, definition);
        setIdentifier(identifier);
        setContacts(contacts);
    }

    public VocabularyWord(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier) {
        this(name, definition, identifier);
        setControlledVocabulary(controlledVocabulary);
    }

    public VocabularyWord(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier, List<String> contacts) {
        this(name, definition, identifier, contacts);
        setControlledVocabulary(controlledVocabulary);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ControlledVocabulary getControlledVocabulary() {
        return controlledVocabulary;
    }

    public void setControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        this.controlledVocabulary = controlledVocabulary;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

}
