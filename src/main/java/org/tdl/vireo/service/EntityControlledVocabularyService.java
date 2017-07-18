package org.tdl.vireo.service;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EntityControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.EntityControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

@Service
public class EntityControlledVocabularyService {

    private final static Logger logger = LoggerFactory.getLogger(EntityControlledVocabularyService.class);

    @Autowired
    private LanguageRepo langaugeRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    private Map<String, EntityControlledVocabularyRepo<EntityControlledVocabulary>> entityControlledVocabularyRepos;

    public EntityControlledVocabularyService() {
        entityControlledVocabularyRepos = new HashMap<String, EntityControlledVocabularyRepo<EntityControlledVocabulary>>();
    }

    @SuppressWarnings("unchecked")
    public void scanForEntityControlledVocabularies(ApplicationReadyEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Language language = langaugeRepo.findAll().get(0);
        Arrays.asList(context.getBeanDefinitionNames()).forEach(name -> {
            Object bean = context.getBean(name);
            if (bean instanceof EntityControlledVocabularyRepo) {
                EntityControlledVocabularyRepo<EntityControlledVocabulary> entityControlledVoabularyRepo = (EntityControlledVocabularyRepo<EntityControlledVocabulary>) bean;
                String entityName = getEntity(entityControlledVoabularyRepo).getSimpleName();
                controlledVocabularyRepo.create(entityName, entityName, language);
                entityControlledVocabularyRepos.put(entityName, entityControlledVoabularyRepo);
                logger.info("Created entity controlled vocabulary: " + entityName);
            }
        });
    }

    public List<VocabularyWord> getControlledVocabularyWords(String entityName) {
        List<VocabularyWord> dictionary = new ArrayList<VocabularyWord>();
        EntityControlledVocabularyRepo<EntityControlledVocabulary> entityControlledVoabularyRepo = entityControlledVocabularyRepos.get(entityName);
        if (entityControlledVoabularyRepo != null) {
            entityControlledVoabularyRepo.findAll().forEach(ecv -> {
                dictionary.add(new VocabularyWord(ecv.getControlledName(), ecv.getControlledDefinition(), ecv.getControlledIdentifier(), ecv.getControlledContacts()));
            });
        } else {
            logger.warn("No entity controlled vocabulary " + entityName);
        }
        return dictionary;
    }

    public static Class<?> getEntity(EntityControlledVocabularyRepo<EntityControlledVocabulary> repo) {
        Type clazzes = getGenericType(repo.getClass())[0];
        Type[] jpaClass = getGenericType(getClass(clazzes));
        return getClass(((ParameterizedType) jpaClass[0]).getActualTypeArguments()[0]);
    }

    public static Type[] getGenericType(Class<?> target) {
        Type[] types = new Type[0];
        if (target != null) {
            types = target.getGenericInterfaces();
            if (types.length == 0) {
                Type type = target.getGenericSuperclass();
                if (type != null) {
                    if (type instanceof ParameterizedType) {
                        types = new Type[] { type };
                    }
                }
            }
        }
        return types;
    }

    private static Class<?> getClass(Type type) {
        Class<?> clazz;
        if (type instanceof Class) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            clazz = getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                clazz = Array.newInstance(componentClass, 0).getClass();
            } else {
                clazz = null;
            }
        } else {
            clazz = null;
        }
        return clazz;
    }

}
