package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.ControlledVocabularyCache;

/**
 * Service to cache and synchronize cache of controlled vocabulary.
 * 
 */
@Service
public class ControlledVocabularyCachingService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Value("${app.cvcache.duration}")
    private Long duration;
    
    private Map<String, ControlledVocabularyCache> cvCacheMap = new HashMap<String, ControlledVocabularyCache>();

    /**
     * 
     * @param cvCache
     */
    public void addControlledVocabularyCache(ControlledVocabularyCache cvCache) {
        cvCacheMap.put(cvCache.getControlledVocabularyName(), cvCache);
    }
    
    /**
     * 
     * @param controlledVocabularyName
     */
    public void removeControlledVocabularyCache(String controlledVocabularyName) { 
        cvCacheMap.remove(controlledVocabularyName);
    }
    
    /**
     * 
     * @param controlledVocabularyName
     * @return
     */
    public ControlledVocabularyCache getControlledVocabularyCache(String controlledVocabularyName) { 
        return cvCacheMap.get(controlledVocabularyName);
    }
    
    /**
     * 
     * @param controlledVocabularyName
     * @return
     */
    public boolean doesControlledVocabularyExist(String controlledVocabularyName) {
        return cvCacheMap.get(controlledVocabularyName) != null;
    }
    
    /**
     * 
     */
    public void clearCache() {
        cvCacheMap = new HashMap<String, ControlledVocabularyCache>();
    }
    
    /**
     * 
     */
    @Scheduled(fixedDelay = 1800000)
    public void cleanCache() {
        List<String> expired = new ArrayList<String>();        
        Long now = new Date().getTime();        
        cvCacheMap.values().parallelStream().forEach(cvCache -> {            
            Long expiration = cvCache.getTimestamp() + duration;
            if(expiration >= now) {
                expired.add(cvCache.getControlledVocabularyName());
            }
        });        
        expired.parallelStream().forEach(cvCacheKey -> {
            cvCacheMap.remove(cvCacheKey);
        });
    }

}
