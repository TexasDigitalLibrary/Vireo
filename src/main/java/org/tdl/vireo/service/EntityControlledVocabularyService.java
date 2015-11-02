package org.tdl.vireo.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EntityCVWhitelist;
import org.tdl.vireo.model.repo.EntityCVWhitelistRepo;

/**
 * Service in which provides management and selection of controlled vocabulary from 
 * any class managed by the entity manager. Allows for enable/disabling the ability to 
 * select controlled vocabulary from any given entity using a whitelist. The controlled vocabulary
 * is returned as a unique list of the datatype of the entity property.
 * 
 */
@Service
public class EntityControlledVocabularyService {
    
    // default whitelist, Embargo guarantor and Attatchment type
    private static final EntityCVWhitelist[] defaultWhitelistedCV = new EntityCVWhitelist[] {
            new EntityCVWhitelist("Embargo", Arrays.asList(new String[] {"guarantor"})), 
            new EntityCVWhitelist("Attachment", Arrays.asList(new String[] {"type"}))
    };

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private EntityCVWhitelistRepo entityCVWhitelistRepo;
    
    // cached entity names
    private List<String> entityNames = null;
    
    // whitelist, which entities properties are enabled to be selected as a controlled vocabulary
    private Map<String, List<String>> whitelist = null;
    
    /**
     * default contructor
     */
    public EntityControlledVocabularyService() { }
    
    /**
     * Method to add all properties of an entity to be selectable as controlled vocabulary.
     * Validates that the entityName is an actual entity. Puts the entity with all its properties
     * in the whitelist. Persists the entity and properties in the EntityCVWhitelistRepo.
     * 
     * @param entityName
     *          String which matches the class name of an entity in which whitelist
     * @throws ClassNotFoundException
     *          thrown when an entityName does not match a class, never thrown
     */
    public void addEntityToWhitelist(String entityName) throws ClassNotFoundException {
        if(entityNames.contains(entityName)) {
            if(whitelist.get(entityName) == null) {
                
                List<String> propertyNames = getPropertyNames(entityName);
                
                whitelist.put(entityName, propertyNames);
                
                if(entityCVWhitelistRepo.findByEntityName(entityName) != null) {
                    entityCVWhitelistRepo.create(entityName, propertyNames);
                }
            }
        }
        else {
            System.out.println("Entity " + entityName + " is not an available entity!\n");
        }
    }
    
    /**
     * Method to remove an entity and all its properties from being able to be selected as a
     * controlled vocabulary. Removes entity and properties from the whitelist and the persistance
     * in the EntityCVWhitelistRepo.
     * 
     * @param entityName
     *          String which matches the class name of an entity in which to remove
     */
    public void removeEntityFromWhitelist(String entityName) {
        whitelist.remove(entityName);
        entityCVWhitelistRepo.deleteByEntityName(entityName);
    }
    
    /**
     * Method to add a property of an entity to be selectable as controlled vocabulary.
     * Validates that the entityName is an actual entity. Validates the propertyName 
     * from the given properties of an entity from the entity manager. Creates an
     * EntityCVWhitelist if none exist for entityName. Otherwise adds property to
     * EntityCVWhitelist and saves to the EntityCVWhitelistRepo. Updates whitelist.
     * 
     * @param entityName
     *          String which matches the class name of an entity in which to add one of its properties
     * @param propertyName
     *          String which matches a property of an entity in which to whitelist
     * @throws ClassNotFoundException
     *          thrown when an entityType does not match a class, never thrown
     */
    public void addEntityPropertyToWhitelist(String entityName, String propertyName) throws ClassNotFoundException {
        List<String> propertyNames = new ArrayList<String>();        
        if(whitelist.get(entityName) == null) {
            if(getPropertyNames(entityName).contains(propertyName)) {
                propertyNames.add(propertyName);
            }
            else {
                System.out.println("Property " + propertyName + " is not an available property on entity " + entityName + "!\n");
                return;
            }
            whitelist.put(entityName, propertyNames);
            
            EntityCVWhitelist entityCVWhitelist;
            if((entityCVWhitelist = entityCVWhitelistRepo.findByEntityName(entityName)) == null) {
                entityCVWhitelistRepo.create(entityName, propertyNames);
            }
            else {
                entityCVWhitelist.addPropertyName(propertyName);
                entityCVWhitelistRepo.save(entityCVWhitelist);
            }
        }
    }
    
    /**
     * Method to remove a property of an entity from being selectable as controlled vocabulary.
     * Removes property from EntityCVWhitelist and updates whitelist.
     * 
     * @param entityName
     *          String which matches the class name of an entity in which to remove one of its properties
     * @param propertyName
     *          String which matches a property of an entity in which to remove from whitelist
     */
    public void removeEntityPropertyFromWhitelist(String entityName, String propertyName) {
        List<String> propertyNames = whitelist.get(entityName);
        if(propertyNames != null) {
            propertyNames.remove(propertyName);
            // may not be needed! pointer?
            whitelist.put(entityName, propertyNames);
            
            EntityCVWhitelist entityCVWhitelist;
            if((entityCVWhitelist = entityCVWhitelistRepo.findByEntityName(entityName)) != null) {
                entityCVWhitelist.getPropertyNames().remove(propertyName);
                entityCVWhitelistRepo.save(entityCVWhitelist);
            }
        }
        else {
            System.out.println("Entity " + entityName + " is not an available entity!\n");
        }
    }
        
    /**
     * Method to retrieve a controlled vocabulary from a property of an entity. The entity must be
     * managed by the entity manager and both the entity and property must be whitelisted.
     * 
     * @param entity
     *          Class<?> generic class of the entity in which to get a controlled vocabulary of one of its property
     * @param property
     *          String which matches a property of the entity in which to get a controlled vocabulary of
     * @return List<Object>
     *          return generic list of objects that is castable to the properties datatype
     */
    public List<Object> getControlledVocabulary(Class<?> entity, String property) {
        
        Metamodel meta = entityManager.getMetamodel();
        EntityType<?> entityType = meta.entity(entity);
        
        String entityName = entityType.getName();
 
        if(whitelist == null) {
            
            whitelist = new HashMap<String, List<String>>();
            
            if(entityCVWhitelistRepo.count() > 0) {
                entityCVWhitelistRepo.findAll().forEach(entityCVWhitelist -> {
                    whitelist.put(entityCVWhitelist.getEntityName(), entityCVWhitelist.getPropertyNames());
                });                
            }
            else {
                for(EntityCVWhitelist ecvw : defaultWhitelistedCV) {
                    whitelist.put(ecvw.getEntityName(), ecvw.getPropertyNames());                    
                    entityCVWhitelistRepo.create(ecvw.getEntityName(), ecvw.getPropertyNames());
                }
                
                System.out.println("\n\nDEFAULT WHITELIST:\n" + whitelist + "\n\n");
            }            
           
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
        
        System.out.println("Entity " + entityName + " with property " + property + " not allowed to be a controlled vocabulary!\n");

        // return empty array list
        return new ArrayList<Object>();
    }
    
    /**
     * Convenience method for the above method to retrieve the controlled vocabulary of a property of an entity.
     * 
     * @param entityName
     *          String which matches the class name of an entity in which to retrieve a controlled vocabulary of one of its proprties 
     * @param property
     *          String which matches a property of the entity in which to get a controlled vocabulary of
     * @return List<Object>
     *          generic list of objects that is castable to the properties datatype
     *          
     * @throws ClassNotFoundException
     *          thrown when an entityType does not match a class
     */
    public List<?> getControlledVocabulary(String entityName, String property) throws ClassNotFoundException {
        return getControlledVocabulary(Class.forName("org.tdl.vireo.model." + entityName), property);
    }

    /**
     * Method in which either returns the cached entity names or retrieves the entity names from the 
     * entity manager.
     *
     * @return List<String>
     *          list of all entity names managed
     */
    public List<String> getEntityNames() {
        if(this.entityNames != null) return this.entityNames;
        List<String> entityNames = new ArrayList<String>();        
        entityManager.getMetamodel().getEntities().parallelStream().forEach(entity -> {
            entityNames.add(entity.getName()); 
        });
        this.entityNames = entityNames;
        return entityNames;
    }
    
    /**
     * Method in which returns all entities managed and all their properties.
     * 
     * @return Map<String, List<String>>
     *          entity names as key and list of entities properties as values
     */
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

    /**
     * Method to get the property names of an entity.
     * 
     * @param entity
     *          Class<?> generic class of the entity in which to get a list its property names
     * @return List<String>
     *          list of entities property names
     */
    public List<String> getPropertyNames(Class<?> entity) {
        List<String> propertyNames = new ArrayList<String>();
        for(Field field : entity.getDeclaredFields()) {
            propertyNames.add(field.getName());
        }
        return propertyNames;
    }
    
    /**
     * Convinience method for the above method to return property names of an entity
     * 
     * @param entityName
     *          String which matches the class name of an entity in which to retrieve its property names
     * @return List<String
     *          list of entities property names   
     * @throws ClassNotFoundException
     *          thrown when an entityType does not match a class
     */
    public List<String> getPropertyNames(String entityName) throws ClassNotFoundException {
        return getPropertyNames(Class.forName("org.tdl.vireo.model." + entityName));
    }
    
    /**
     * Method to get the current whitelist. A map of the enabled properties of entities that can 
     * be selected a controlled vocabulary.
     * 
     * @return Map<String, List<String>>
     *          entity names as key and list of entities properties as values
     */
    public Map<String, List<String>> getWhitelist() {
        return whitelist;
    }
    
}
