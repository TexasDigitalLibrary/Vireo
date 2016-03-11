package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.ControlledVocabularyCache;

@Service
public class ControlledVocabularyCachingService {
    
    @Value("${app.cvcache.duration}")
    private Long duration;

    
    private Map<String, ControlledVocabularyCache> cvCacheMap = new HashMap<String, ControlledVocabularyCache>();
    
    private String generateCacheKey(String requestEmail, Long controlledVocabularyId) {
        return requestEmail + "-" + controlledVocabularyId;
    }
    
    public void addControlledVocabularyCache(ControlledVocabularyCache cvCache) {
        cvCacheMap.put(generateCacheKey(cvCache.getRequestEmail(), cvCache.getControlledVocabularyId()), cvCache);
    }
    
    public void removeControlledVocabularyCache(String requestEmail, Long controlledVocabularyId) { 
        cvCacheMap.remove(generateCacheKey(requestEmail, controlledVocabularyId));
    }
    
    public ControlledVocabularyCache getControlledVocabularyCache(String requestEmail, Long controlledVocabularyId) { 
        return cvCacheMap.get(generateCacheKey(requestEmail, controlledVocabularyId));
    }
    
    @Scheduled(fixedDelay = 1800000)
    public void cleanCache() {
        List<String> expired = new ArrayList<String>();        
        Long now = new Date().getTime();        
        cvCacheMap.values().parallelStream().forEach(cvCache -> {            
            Long expiration = cvCache.getTimestamp() + duration;
            if(expiration >= now) {
                expired.add(generateCacheKey(cvCache.getRequestEmail(), cvCache.getControlledVocabularyId()));
            }
        });        
        expired.parallelStream().forEach(cvCacheKey -> {
            cvCacheMap.remove(cvCacheKey);
        });
    }

}
