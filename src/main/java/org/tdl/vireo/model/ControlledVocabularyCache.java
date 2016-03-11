package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

public class ControlledVocabularyCache {
    
    private Long timestamp;
    
    private Long controlledVocabularyId;
    
    private String requestEmail;
    
    private List<VocabularyWord> newVocabularyWords;
    
    private List<VocabularyWord[]> updatingVocabularyWords;
    
    private List<VocabularyWord> duplicateVocabularyWords;

    public ControlledVocabularyCache() {
        setNewVocabularyWords(new ArrayList<VocabularyWord>());
        setUpdatingVocabularyWords(new ArrayList<VocabularyWord[]>());
        setDuplicateVocabularyWords(new ArrayList<VocabularyWord>());
    }
    
    public ControlledVocabularyCache(Long timestamp, Long controlledVocabularyId, String requestEmail) {
        this();
        setTimestamp(timestamp);
        setControlledVocabularyId(controlledVocabularyId);
        setRequestEmail(requestEmail);        
    }
    
    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the controlledVocabularyId
     */
    public Long getControlledVocabularyId() {
        return controlledVocabularyId;
    }

    /**
     * @param controlledVocabularyId the controlledVocabularyId to set
     */
    public void setControlledVocabularyId(Long controlledVocabularyId) {
        this.controlledVocabularyId = controlledVocabularyId;
    }

    /**
     * @return the requestEmail
     */
    public String getRequestEmail() {
        return requestEmail;
    }

    /**
     * @param requestEmail the requestEmail to set
     */
    public void setRequestEmail(String requestEmail) {
        this.requestEmail = requestEmail;
    }

    /**
     * @return the newVocabularyWords
     */
    public List<VocabularyWord> getNewVocabularyWords() {
        return newVocabularyWords;
    }

    /**
     * @param newVocabularyWords the newVocabularyWords to set
     */
    public void setNewVocabularyWords(List<VocabularyWord> newVocabularyWords) {
        this.newVocabularyWords = newVocabularyWords;
    }

    /**
     * @return the updatingVocabularyWords
     */
    public List<VocabularyWord[]> getUpdatingVocabularyWords() {
        return updatingVocabularyWords;
    }

    /**
     * @param updatingVocabularyWords the updatingVocabularyWords to set
     */
    public void setUpdatingVocabularyWords(List<VocabularyWord[]> updatingVocabularyWords) {
        this.updatingVocabularyWords = updatingVocabularyWords;
    }

    /**
     * @return the duplicateVocabularyWords
     */
    public List<VocabularyWord> getDuplicateVocabularyWords() {
        return duplicateVocabularyWords;
    }

    /**
     * @param duplicateVocabularyWords the duplicateVocabularyWords to set
     */
    public void setDuplicateVocabularyWords(List<VocabularyWord> duplicateVocabularyWords) {
        this.duplicateVocabularyWords = duplicateVocabularyWords;
    }
    
}
