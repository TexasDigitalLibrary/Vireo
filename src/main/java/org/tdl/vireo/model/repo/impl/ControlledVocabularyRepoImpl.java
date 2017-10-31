package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.CHANGE;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;
import edu.tamu.weaver.response.ApiResponse;

public class ControlledVocabularyRepoImpl extends AbstractWeaverOrderedRepoImpl<ControlledVocabulary, ControlledVocabularyRepo> implements ControlledVocabularyRepoCustom {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByNameAndLanguage(name, language);
        if (controlledVocabulary == null) {
            controlledVocabulary = new ControlledVocabulary(name, language);
            controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
            controlledVocabulary = super.create(controlledVocabulary);
            simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
        }
        return controlledVocabulary;
    }

    @Override
    public ControlledVocabulary create(String name, Language language, Boolean isEntityProperty) {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByNameAndLanguageAndIsEntityProperty(name, language, isEntityProperty);
        if (controlledVocabulary == null) {
            controlledVocabulary = new ControlledVocabulary(name, language, isEntityProperty);
            controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
            controlledVocabulary = super.create(controlledVocabulary);
            simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
        }

        return controlledVocabulary;
    }

    @Override
    public ControlledVocabulary update(ControlledVocabulary cv) {
        cv = super.update(cv);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
        return cv;
    }

    @Override
    public void delete(ControlledVocabulary cv) {
        super.delete(cv);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
    }

    @Override
    @Transactional
    public void reorder(Long src, Long dest) {
        super.reorder(src, dest);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
    }

    @Override
    @Transactional
    public void sort(String column) {
        super.sort(column);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
    }

    @Override
    @Transactional
    public void remove(ControlledVocabulary cv) {
        super.remove(cv);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CHANGE));
    }

    @Override
    public Class<?> getModelClass() {
        return ControlledVocabulary.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/controlled-vocabulary";
    }

}
