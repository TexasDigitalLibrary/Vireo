package org.tdl.vireo.model.repo;

import java.util.List;
import java.util.Map;

public interface EntityControlledVocabularyInterface {
    
    List<?> getControlledVocabulary(Class<?> entity, String property);
    
    List<?> getControlledVocabulary(String entityName, String property) throws ClassNotFoundException;
    
    List<String> getEntityNames();
    
    Map<String, List<String>> getAllEntityPropertyNames();
    
    List<String> getPropertyNames(Class<?> entity);
    
    List<String> getPropertyNames(String entityName) throws ClassNotFoundException;
    
}
