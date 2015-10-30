package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "controlled_vocabulary_id" }) )
public class VocabularyWord extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = true)
    private String definition;
    
    @Column(nullable = true)
    private String identifier;
    
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE })
    private ControlledVocabulary controlledVocabulary;
    
    public VocabularyWord() { }
    
    public VocabularyWord(String name) {
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
    
}
