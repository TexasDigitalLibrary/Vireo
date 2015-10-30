package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import org.tdl.vireo.config.SpringContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Configurable;
import org.tdl.vireo.service.EntityControlledVocabularyService;

@Entity
@Configurable
public class ControlledVocabulary extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = true, unique = true)
    private String entityName;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private Language language;

    @ElementCollection
    @Column(nullable = true, unique = true)
    private Set<String> values;
    
    @Column(nullable = false)
    private Boolean isEntityProperty;

    public ControlledVocabulary() {        
        setIsEntityProperty(false);
        setValues(new TreeSet<String>());
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
     * 
     * @param name
     * @param entityName
     * @param language
     */
    public ControlledVocabulary(String name, String entityName, Language language) {
        this();
        setName(name);
        setEntityName(entityName);
        setLanguage(language);
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
     * @return the values
     */
    public Set<String> getValues() {        
        if(isEntityProperty()) {
            List<String> values = new ArrayList<String>();
            try {
                
                EntityControlledVocabularyService entityControlledVocabularyService = SpringContext.bean(EntityControlledVocabularyService.class);
                
                entityControlledVocabularyService.getControlledVocabulary(entityName, name).parallelStream().forEach(property -> {
                    values.add(property.toString());
                });
                
            }
            catch(ClassNotFoundException e) {
                System.out.println("Entity " + entityName + " not found!\n");
            }
            return values.isEmpty() ? new HashSet<>() : new HashSet<>(values);
        }
        return this.values;
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
        if(!isEntityProperty()) {
            this.values = values;
        }
    }

    /**
     * 
     * @param value
     */
    public void addValue(String value) {
        if(!isEntityProperty()) {
            getValues().add(value);
        }
    }

    /**
     * 
     * @param value
     */
    public void removeValue(String value) {
        if(!isEntityProperty()) {
            getValues().remove(value);
        }
    }

    /**
     * 
     * @param value
     * @return
     */
    public String getValueByValue(String value) {
        for (String v : getValues()) {
            if (v.equals(value))
                return v;
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public Boolean isEntityProperty() {
        return isEntityProperty;
    }

    /**
     * 
     * @param isEntityProperty
     */
    public void setIsEntityProperty(Boolean isEntityProperty) {
        this.isEntityProperty = isEntityProperty;
    }
    
}
