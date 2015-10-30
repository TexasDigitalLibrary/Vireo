package org.tdl.vireo.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.springframework.stereotype.Service;

@Service
public class EntityControlledVocabularyService {

    @PersistenceContext
    private EntityManager entityManager;
    
    private List<String> entityNames = null;
    
    private Map<String, List<String>> whitelist = null;
    
    public void addEntityToWhitelist(String entityName) throws ClassNotFoundException {
        if(entityNames.contains(entityName)) {
            if(whitelist.get(entityName) == null) {
                whitelist.put(entityName, getPropertyNames(entityName));
            }
        }
        else {
            System.out.println("Entity " + entityName + " is not an available entity!");
        }
    }
    
    public void removeEntityFromWhitelist(String entityName) {
        whitelist.remove(entityName);
    }
    
    public void addEntityPropertyToWhitelist(String entityName, String propertyName) throws ClassNotFoundException {
        List<String> properties = new ArrayList<String>();        
        if(whitelist.get(entityName) == null) {
            properties = getPropertyNames(entityName);
            if(!properties.contains(propertyName)) {
                properties.add(propertyName);
            }
            else {
                System.out.println("Property " + propertyName + " is not an available property on entity " + entityName + "!");
                return;
            }
            whitelist.put(entityName, properties);
        }
    }
    
    public void removeEntityPropertyFromWhitelist(String entityName, String propertyName) {
        List<String> properties = whitelist.get(entityName);
        if(properties != null) {
            properties.remove(propertyName);
        }
        else {
            System.out.println("Entity " + entityName + " is not an available entity!");
        }
    }
        
    public List<?> getControlledVocabulary(Class<?> entity, String property) {
        
        Metamodel meta = entityManager.getMetamodel();
        EntityType<?> entityType = meta.entity(entity);
        
        String entityName = entityType.getName();
 
        if(whitelist == null) {
            
            // none whitelisted by default
            //whitelist = new HashMap<String, List<String>>();
            
            // all whitelisted by default
            whitelist = getAllEntityPropertyNames();
        }
        
        List<String> properties = whitelist.get(entityName);
       
        if(properties != null) {
            for(String field : properties) {
                if(field.equals(property)) {
                    CriteriaBuilder builder = entityManager.getCriteriaBuilder();                    
                    CriteriaQuery<Object> query = builder.createQuery();                    
                    Root<?> root = query.from(entity);                    
                    query.multiselect(root.get(property)).distinct(true);                    
                    return entityManager.createQuery(query).getResultList();
                }
            }
        }
        
        System.out.println("Entity " + entityName + " with property " + property + " not allowed to be a controlled vocabulary!");

        // return empty array list
        return new ArrayList<Object>();
    }
    
    public List<?> getControlledVocabulary(String entityName, String property) throws ClassNotFoundException {
        return getControlledVocabulary(Class.forName("org.tdl.vireo.model." + entityName), property);
    }

    public List<String> getEntityNames() {
        if(this.entityNames != null) return this.entityNames;
        List<String> entityNames = new ArrayList<String>();        
        entityManager.getMetamodel().getEntities().parallelStream().forEach(entity -> {
            entityNames.add(entity.getName()); 
        });
        this.entityNames = entityNames;
        return entityNames;
    }
    
    public Map<String, List<String>> getAllEntityPropertyNames() {        
        Map<String, List<String>> propertyMap = new HashMap<String, List<String>>();               
        entityManager.getMetamodel().getEntities().parallelStream().forEach(entity -> {
            List<String> propertyNames = new ArrayList<String>();
            entity.getAttributes().forEach(attribute -> {
                propertyNames.add(attribute.getName());
            });
            propertyMap.put(entity.getName(), propertyNames);
        });
        return propertyMap;
    }

    public List<String> getPropertyNames(Class<?> entity) {
        List<String> propertyNames = new ArrayList<String>();
        for(Field field : entity.getDeclaredFields()) {
            propertyNames.add(field.getName());
        }
        return propertyNames;
    }
    
    public List<String> getPropertyNames(String entityName) throws ClassNotFoundException {
        return getPropertyNames(Class.forName("org.tdl.vireo.model." + entityName));
    }
    
}
