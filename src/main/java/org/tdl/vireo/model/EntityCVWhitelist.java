package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.EntityCVWhitelistValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class EntityCVWhitelist  extends BaseEntity {
    
    @Column(unique = true)
    private String entityName;
    
    @ElementCollection(fetch = EAGER)
    private List<String> propertyNames;
    
    public EntityCVWhitelist() {
        setModelValidator(new EntityCVWhitelistValidator());
    }
    
    public EntityCVWhitelist(String entityName) {
        this();
        setEntityName(entityName);
        setPropertyNames(new ArrayList<String>());
    }
    
    public EntityCVWhitelist(String entityName, List<String> propertyNames) {
        setEntityName(entityName);
        setPropertyNames(propertyNames);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }
    
    public void addPropertyName(String propertyName) {
        if(!propertyNames.contains(propertyName)) {
            propertyNames.add(propertyName);
        }
    }
    
    public void removePropertyName(String propertyName) {
        propertyNames.remove(propertyName);
    }
    
}
