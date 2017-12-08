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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.EntityControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.EntityControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

import edu.tamu.weaver.data.utility.EntityUtility;

@Service
public class EntityControlledVocabularyService {

    private final static Logger logger = LoggerFactory.getLogger(EntityControlledVocabularyService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LanguageRepo langaugeRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    private Map<String, EntityControlledVocabularyRepo<EntityControlledVocabulary>> entityControlledVocabularyRepos;

    public EntityControlledVocabularyService() {
        entityControlledVocabularyRepos = new HashMap<String, EntityControlledVocabularyRepo<EntityControlledVocabulary>>();
    }

    @SuppressWarnings("unchecked")
    public void scanForEntityControlledVocabularies() throws ClassNotFoundException {
        Language language = langaugeRepo.findAll().get(0);
        for (String name : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(name);
            if (bean instanceof EntityControlledVocabularyRepo) {
                EntityControlledVocabularyRepo<EntityControlledVocabulary> entityControlledVoabularyRepo = (EntityControlledVocabularyRepo<EntityControlledVocabulary>) bean;
                String entityName = getEntity(entityControlledVoabularyRepo).getSimpleName();
                List<EntityCV.Subset> subsetAnnotations = getEntityControlledVocabularySubsets(entityControlledVoabularyRepo);
                if (subsetAnnotations.size() > 0) {
                    subsetAnnotations.forEach(entityCVSubset -> {
                        String controlledVocabularyName = entityCVSubset.name();
                        controlledVocabularyRepo.create(controlledVocabularyName, language, true);
                        entityControlledVocabularyRepos.put(controlledVocabularyName, entityControlledVoabularyRepo);
                        logger.info("Created entity controlled vocabulary: " + controlledVocabularyName);
                    });
                } else {
                    Optional<String> controlledVocabularyName = getEntityControlledVocabularyName(entityControlledVoabularyRepo);
                    String entityCVName = controlledVocabularyName.isPresent() ? controlledVocabularyName.get() : entityName;
                    controlledVocabularyRepo.create(entityCVName, language, true);
                    entityControlledVocabularyRepos.put(entityCVName, entityControlledVoabularyRepo);
                    logger.info("Created entity controlled vocabulary: " + entityCVName);
                }
            }
        }
    }

    public List<VocabularyWord> getControlledVocabularyWords(String name) throws ClassNotFoundException {
        List<VocabularyWord> dictionary = new ArrayList<VocabularyWord>();
        EntityControlledVocabularyRepo<EntityControlledVocabulary> entityControlledVoabularyRepo = entityControlledVocabularyRepos.get(name);
        if (entityControlledVoabularyRepo != null) {
            List<EntityCV.Subset> subsets = getEntityControlledVocabularySubsets(entityControlledVoabularyRepo);
            List<EntityCV.Filter> filters = new ArrayList<EntityCV.Filter>();
            for (EntityCV.Subset subset : subsets) {
                if (subset.name().equals(name)) {
                    filters = Arrays.asList(subset.filters());
                    break;
                }
            }
            for (EntityControlledVocabulary ecv : entityControlledVoabularyRepo.findAll()) {
                boolean include = true;
                for (EntityCV.Filter filter : filters) {
                    Object actualValue = EntityUtility.getValueFromPath(ecv, filter.path().split("\\."));
                    if (!actualValue.toString().equals(filter.value())) {
                        include = false;
                        break;
                    }
                }
                if (include) {
                    dictionary.add(new VocabularyWord(ecv.getControlledName(), ecv.getControlledDefinition(), ecv.getControlledIdentifier(), ecv.getControlledContacts()));
                }
            }
        } else {
            logger.warn("No entity controlled vocabulary " + name);
        }
        return dictionary;
    }

    public List<EntityCV.Subset> getEntityControlledVocabularySubsets(EntityControlledVocabularyRepo<EntityControlledVocabulary> repo) throws ClassNotFoundException {
        List<EntityCV.Subset> subsets = new ArrayList<EntityCV.Subset>();
        EntityCV annotation = Class.forName(getGenericType(repo.getClass())[0].getTypeName()).getDeclaredAnnotation(EntityCV.class);
        if (annotation != null) {
            subsets = Arrays.asList(annotation.subsets());
        }
        return subsets;
    }

    public Optional<String> getEntityControlledVocabularyName(EntityControlledVocabularyRepo<EntityControlledVocabulary> repo) throws ClassNotFoundException {
        Optional<String> name = Optional.empty();
        EntityCV annotation = Class.forName(getGenericType(repo.getClass())[0].getTypeName()).getDeclaredAnnotation(EntityCV.class);
        if (annotation != null) {
            name = Optional.of(annotation.name());
        } else {
            logger.warn("No name provided for entity controlled vocabulary repo.");
        }
        return name;
    }

    public Class<?> getEntity(EntityControlledVocabularyRepo<EntityControlledVocabulary> repo) {
        Type type = getGenericType(repo.getClass())[0];
        Type[] types = getGenericType(getClass(type));
        return getClass(((ParameterizedType) types[0]).getActualTypeArguments()[0]);
    }

    public Type[] getGenericType(Class<?> target) {
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

    private Class<?> getClass(Type type) {
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
