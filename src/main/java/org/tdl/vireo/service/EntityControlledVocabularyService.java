package org.tdl.vireo.service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.AppContextInitializedHandler;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.EntityCVWhitelist;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.EntityCVWhitelistRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

/**
 * Service in which provides management and selection of controlled vocabulary from any class managed by the entity manager. Allows for enable/disabling the ability to select controlled vocabulary from any given entity using a whitelist. The controlled vocabulary is returned as a unique list of the datatype of the entity property.
 *
 */
@Service
public class EntityControlledVocabularyService {

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);

    // default whitelist consists only of Embargo guarantor
    private static final EntityCVWhitelist[] defaultWhitelistedCV = new EntityCVWhitelist[] { new EntityCVWhitelist("Embargo", Arrays.asList(new String[] { "guarantor" })), };

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityCVWhitelistRepo entityCVWhitelistRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private LanguageRepo languageRepo;

    // cached entity names
    private List<String> entityNames = null;

    // whitelist, which entities properties are enabled to be selected as a controlled vocabulary
    private Map<String, List<String>> whitelist = null;

    /**
     * default contructor
     */
    public EntityControlledVocabularyService() {
        whitelist = new HashMap<String, List<String>>();
    }

    public void init() {
        for (EntityCVWhitelist ecvw : defaultWhitelistedCV) {

            String ecvwName = ecvw.getEntityName();
            List<String> ecvwPropertyNames = ecvw.getPropertyNames();

            whitelist.put(ecvwName, ecvwPropertyNames);

            if (entityCVWhitelistRepo.findByEntityName(ecvwName) == null) {
                entityCVWhitelistRepo.create(ecvwName, ecvwPropertyNames);
            }

            ecvwPropertyNames.forEach(propertyName -> {

                // TODO: manage default language accordingly and handle duplicate controlled vocabulary names on different entities
                Language language = languageRepo.findByName("English");

                ControlledVocabulary cv = controlledVocabularyRepo.findByNameAndLanguage(ecvwName + "." + propertyName, language);

                if (cv == null) {
                    cv = controlledVocabularyRepo.create(ecvwName + "." + propertyName, ecvwName, language);
                }

                if (isPropertyEnum(ecvwName, propertyName)) {
                    cv.setIsEnum(true);
                    controlledVocabularyRepo.save(cv);
                }
            });

        }

        logger.info("\n\nDEFAULT WHITELIST:\n" + whitelist + "\n\n");
    }

    private boolean isPropertyEnum(String entityName, String propertyName) {
        boolean isEnum = false;

        try {
            Class<?> clazz = Class.forName("org.tdl.vireo.model." + entityName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(propertyName)) {
                    Type type = field.getGenericType();
                    if (type instanceof Class && ((Class<?>) type).isEnum()) {
                        isEnum = true;
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return isEnum;
    }

    /**
     * Method to add all properties of an entity to be selectable as controlled vocabulary. Validates that the entityName is an actual entity. Puts the entity with all its properties in the whitelist. Persists the entity and properties in the EntityCVWhitelistRepo.
     *
     * @param entityName
     *            String which matches the class name of an entity in which whitelist
     * @throws ClassNotFoundException
     *             thrown when an entityName does not match a class, never thrown
     */
    public void addEntityToWhitelist(String entityName) throws ClassNotFoundException {
        if (entityNames.contains(entityName)) {
            if (whitelist.get(entityName) == null) {

                List<String> propertyNames = getPropertyNames(entityName);

                whitelist.put(entityName, propertyNames);

                if (entityCVWhitelistRepo.findByEntityName(entityName) != null) {
                    entityCVWhitelistRepo.create(entityName, propertyNames);
                }
            }
        } else {
            logger.info("Entity " + entityName + " is not an available entity!\n");
        }
    }

    /**
     * Method to remove an entity and all its properties from being able to be selected as a controlled vocabulary. Removes entity and properties from the whitelist and the persistance in the EntityCVWhitelistRepo.
     *
     * @param entityName
     *            String which matches the class name of an entity in which to remove
     */
    public void removeEntityFromWhitelist(String entityName) {
        whitelist.remove(entityName);
        entityCVWhitelistRepo.deleteByEntityName(entityName);
    }

    /**
     * Method to add a property of an entity to be selectable as controlled vocabulary. Validates that the entityName is an actual entity. Validates the propertyName from the given properties of an entity from the entity manager. Creates an EntityCVWhitelist if none exist for entityName. Otherwise adds property to EntityCVWhitelist and saves to the EntityCVWhitelistRepo. Updates whitelist.
     *
     * @param entityName
     *            String which matches the class name of an entity in which to add one of its properties
     * @param propertyName
     *            String which matches a property of an entity in which to whitelist
     * @throws ClassNotFoundException
     *             thrown when an entityType does not match a class, never thrown
     */
    public void addEntityPropertyToWhitelist(String entityName, String propertyName) throws ClassNotFoundException {
        List<String> propertyNames = new ArrayList<String>();
        if (whitelist.get(entityName) == null) {
            if (getPropertyNames(entityName).contains(propertyName)) {
                propertyNames.add(propertyName);
            } else {
                logger.info("Property " + propertyName + " is not an available property on entity " + entityName + "!\n");
                return;
            }
            whitelist.put(entityName, propertyNames);

            EntityCVWhitelist entityCVWhitelist;
            if ((entityCVWhitelist = entityCVWhitelistRepo.findByEntityName(entityName)) == null) {
                entityCVWhitelistRepo.create(entityName, propertyNames);
            } else {
                entityCVWhitelist.addPropertyName(propertyName);
                entityCVWhitelistRepo.save(entityCVWhitelist);
            }
        }
    }

    /**
     * Method to remove a property of an entity from being selectable as controlled vocabulary. Removes property from EntityCVWhitelist and updates whitelist.
     *
     * @param entityName
     *            String which matches the class name of an entity in which to remove one of its properties
     * @param propertyName
     *            String which matches a property of an entity in which to remove from whitelist
     */
    public void removeEntityPropertyFromWhitelist(String entityName, String propertyName) {
        List<String> propertyNames = whitelist.get(entityName);
        if (propertyNames != null) {
            propertyNames.remove(propertyName);
            // may not be needed! pointer?
            whitelist.put(entityName, propertyNames);

            EntityCVWhitelist entityCVWhitelist;
            if ((entityCVWhitelist = entityCVWhitelistRepo.findByEntityName(entityName)) != null) {
                entityCVWhitelist.getPropertyNames().remove(propertyName);
                entityCVWhitelistRepo.save(entityCVWhitelist);
            }
        } else {
            logger.info("Entity " + entityName + " is not an available entity!\n");
        }
    }

    /**
     * Method to retrieve a controlled vocabulary from a property of an entity. The entity must be managed by the entity manager and both the entity and property must be whitelisted.
     *
     * @param entity
     *            Class<?> generic class of the entity in which to get a controlled vocabulary of one of its property
     * @param property
     *            String which matches a property of the entity in which to get a controlled vocabulary of
     * @return List<Object> return generic list of objects that is castable to the properties datatype
     */
    public List<VocabularyWord> getControlledVocabulary(Class<?> entity, String property) {

        ArrayList<VocabularyWord> ret = new ArrayList<VocabularyWord>();

        Metamodel meta = entityManager.getMetamodel();
        EntityType<?> entityType = meta.entity(entity);

        String entityName = entityType.getName();

        if (entityCVWhitelistRepo.count() > 0) {
            entityCVWhitelistRepo.findAll().forEach(entityCVWhitelist -> {
                whitelist.put(entityCVWhitelist.getEntityName(), entityCVWhitelist.getPropertyNames());
            });
        } else {
            logger.info("\nThere are no entity controlled vocabularies!\n");
        }

        List<String> properties = whitelist.get(entityName);

        if (properties != null) {
            for (String field : properties) {
                if (field.equals(property)) {
                    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                    CriteriaQuery<Object> query = builder.createQuery();
                    Root<?> root = query.from(entity);
                    query.multiselect(root.get(property)).distinct(true);
                    List<Object> results = entityManager.createQuery(query).getResultList();
                    for (Object row : results) {
                        ret.add(new VocabularyWord(row.toString()));
                    }
                }
            }
        }

        // return empty array list
        return ret;
    }

    /**
     * Convenience method for the above method to retrieve the controlled vocabulary of a property of an entity.
     *
     * @param entityName
     *            String which matches the class name of an entity in which to retrieve a controlled vocabulary of one of its proprties
     * @param property
     *            String which matches a property of the entity in which to get a controlled vocabulary of
     * @return List<Object> generic list of objects that is castable to the properties datatype
     *
     * @throws ClassNotFoundException
     *             thrown when an entityType does not match a class
     */
    public List<VocabularyWord> getControlledVocabulary(String entityName, String property) throws ClassNotFoundException {
        return getControlledVocabulary(Class.forName("org.tdl.vireo.model." + entityName), property);
    }

    /**
     * Method in which either returns the cached entity names or retrieves the entity names from the entity manager.
     *
     * @return List<String> list of all entity names managed
     */
    public List<String> getEntityNames() {
        if (this.entityNames != null)
            return this.entityNames;
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
     * @return Map<String, List<String>> entity names as key and list of entities properties as values
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
     *            Class<?> generic class of the entity in which to get a list its property names
     * @return List<String> list of entities property names
     */
    public List<String> getPropertyNames(Class<?> entity) {
        List<String> propertyNames = new ArrayList<String>();
        for (Field field : entity.getDeclaredFields()) {
            propertyNames.add(field.getName());
        }
        return propertyNames;
    }

    /**
     * Convinience method for the above method to return property names of an entity
     *
     * @param entityName
     *            String which matches the class name of an entity in which to retrieve its property names
     * @return List<String list of entities property names
     * @throws ClassNotFoundException
     *             thrown when an entityType does not match a class
     */
    public List<String> getPropertyNames(String entityName) throws ClassNotFoundException {
        return getPropertyNames(Class.forName("org.tdl.vireo.model." + entityName));
    }

    /**
     * Method to get the current whitelist. A map of the enabled properties of entities that can be selected a controlled vocabulary.
     *
     * @return Map<String, List<String>> entity names as key and list of entities properties as values
     */
    public Map<String, List<String>> getWhitelist() {
        return whitelist;
    }

}
