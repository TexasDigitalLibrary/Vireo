package org.tdl.vireo.model.repo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        entityManager.getMetamodel().getEntities().parallelStream().forEach(et -> {
            entityNames.add(et.getName()); 
        });
        return entityNames;
    }
    
}
