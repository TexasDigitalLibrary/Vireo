package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

public class ControlledVocabularyCache {

    private Long timestamp;

    private String controlledVocabularyName;

    private List<VocabularyWord> newVocabularyWords;

    private List<VocabularyWord[]> updatingVocabularyWords;

    private List<VocabularyWord> duplicateVocabularyWords;

    private List<VocabularyWord> removedVocabularyWords;

    public ControlledVocabularyCache() {
        setNewVocabularyWords(new ArrayList<VocabularyWord>());
        setUpdatingVocabularyWords(new ArrayList<VocabularyWord[]>());
        setDuplicateVocabularyWords(new ArrayList<VocabularyWord>());
        setRemovedVocabularyWords(new ArrayList<VocabularyWord>());
    }

    public ControlledVocabularyCache(Long timestamp, String controlledVocabularyName) {
        this();
        setTimestamp(timestamp);
        setControlledVocabularyName(controlledVocabularyName);
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
     * @return the controlledVocabularyName
     */
    public String getControlledVocabularyName() {
        return controlledVocabularyName;
    }

    /**
     * @param controlledVocabularyName the controlledVocabularyName to set
     */
    public void setControlledVocabularyName(String controlledVocabularyName) {
        this.controlledVocabularyName = controlledVocabularyName;
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

    /**
     * @return the removedVocabularyWords
     */
    public List<VocabularyWord> getRemovedVocabularyWords() {
        return removedVocabularyWords;
    }

    /**
     * @param removedVocabularyWords the removedVocabularyWords to set
     */
    public void setRemovedVocabularyWords(List<VocabularyWord> removedVocabularyWords) {
        this.removedVocabularyWords = removedVocabularyWords;
    }

}
