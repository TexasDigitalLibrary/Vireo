package org.tdl.vireo.model.repo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.springframework.stereotype.Repository;

@Repository
public class EntityControlledVocabularyRepo implements EntityControlledVocabularyInterface {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<?> getControlledVocabulary(Class<?> entity, String property) {
        
        Metamodel meta = entityManager.getMetamodel();
        EntityType<?> entityType = meta.entity(entity);

        Table t = entity.getAnnotation(Table.class);

        String tableName = (t == null) ? entityType.getName().toUpperCase() : t.name();
                
        System.out.println("\nTABLE: " + tableName + "\n");
        System.out.println("\nCOLUMN: " + property + "\n");
        
        for(Field field : entity.getDeclaredFields()) {
            if(field.getName().equals(property)) {
                return entityManager.createNativeQuery("SELECT " + property + " FROM " + tableName).getResultList();
            }
        }
        
        // property not on entity
        return null;
    }

    @Override
    public List<String> getEntityNames() {
        List<String> entityNames = new ArrayList<String>();        
        entityManager.getMetamodel().getEntities().parallelStream().forEach(entity -> {
            entityNames.add(entity.getName()); 
        });
        return entityNames;
    }
    
    @Override
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

    @Override
    public List<String> getPropertyNames(Class<?> entity) {
        List<String> propertyNames = new ArrayList<String>();
        for(Field field : entity.getDeclaredFields()) {
            propertyNames.add(field.getName());
        }
        return propertyNames;
    }
    
}
