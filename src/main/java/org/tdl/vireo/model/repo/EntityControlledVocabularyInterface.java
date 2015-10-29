package org.tdl.vireo.model.repo;

import java.util.List;

public interface EntityControlledVocabularyInterface {
    
    List<?> getControlledVocabulary(Class<?> entity, String property);
    
    List<String> getEntityNames();
    
}
