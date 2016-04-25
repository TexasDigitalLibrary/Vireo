package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
import org.tdl.vireo.config.SpringContext;
import org.tdl.vireo.service.EntityControlledVocabularyService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Configurable
public class ControlledVocabulary extends BaseOrderedEntity {
    
    @Column(nullable = false, unique = true)
    @NotEmpty
    private String name;
    
    @Column(nullable = true, unique = false)
    private String entityName;

    @ManyToOne(cascade = { DETACH, REFRESH }, optional = false)
    @NotNull
    private Language language;
    
    @ManyToMany(cascade = { ALL }, fetch = EAGER)    
    private List<VocabularyWord> dictionary = new ArrayList<VocabularyWord>();
    
    @JsonProperty("isEntityProperty")
    @Column(nullable = false)
    @NotNull
    private Boolean isEntityProperty;
    
    @JsonProperty("isEnum")
    @Column(nullable = false)
    @NotNull
    private Boolean isEnum;

    public ControlledVocabulary() {
        setIsEnum(false);
        setIsEntityProperty(false);
    }

    /**
     * 
     * @param name
     * @param language
     * @param order
     */
    public ControlledVocabulary(String name, Language language) {
        this();
        setName(name);
        setLanguage(language);
    }
    
    /**
     * 
     * @param name
     * @param entityName
     * @param language
     * @param order
     */
    public ControlledVocabulary(String name, String entityName, Language language) {
        this(name, language);
        setEntityName(entityName);
        setIsEntityProperty(true);
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
     * 
     * @return
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * 
     * @param entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Returns either a set of vocabulary words of the controlled vocabulary or a set composed of a unique list
     * of an entities property. This is done lazily by requesting the EntityControlledVocabularyService bean through 
     * a static method of SpringContext. From the bean, calling the getControlledVocabulary method providing the entityName
     * and name of the controlled vocabulary. This name is also the property name of the entity. 
     * 
     * @return the values
     */
    public List<VocabularyWord> getDictionary() {
        List<VocabularyWord> values = new ArrayList<VocabularyWord>();
        if(!isEntityProperty()) {
            values.addAll(dictionary);
        }
        else {
            try {                
                EntityControlledVocabularyService entityControlledVocabularyService = SpringContext.bean(EntityControlledVocabularyService.class);
                values.addAll(entityControlledVocabularyService.getControlledVocabulary(entityName, name));
            }
            catch(ClassNotFoundException e) {
                System.out.println("Entity " + entityName + " not found!\n");
            }
        }
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
    public void setDictionary(List<VocabularyWord> values) {
        if(!isEntityProperty()) {
            dictionary = values;
        }
    }

    /**
     * 
     * @param value
     */
    public void addValue(VocabularyWord value) {
        if(!isEntityProperty() && !dictionary.contains(value)) {
            dictionary.add(value);
        }
    }

    /**
     * 
     * @param value
     */
    public void removeValue(VocabularyWord value) {
        if(!isEntityProperty()) {
            dictionary.remove(value);
        }
    }

    /**
     * 
     * @return
     */
    @JsonIgnore
    public Boolean isEntityProperty() {
        return isEntityProperty;
    }

    /**
     * 
     * @param isEntityProperty
     */
    @JsonIgnore
    public void setIsEntityProperty(Boolean isEntityProperty) {
        this.isEntityProperty = isEntityProperty;
    }

    /**
     * @return the isEnum
     */
    @JsonIgnore
    public Boolean isEnum() {
        return isEnum;
    }

    /**
     * @param isEnum the isEnum to set
     */
    @JsonIgnore
    public void setIsEnum(Boolean isEnum) {
        this.isEnum = isEnum;
    }
    
}
