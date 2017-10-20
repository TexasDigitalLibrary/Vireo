package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.custom.CustomActionDefinitionRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class CustomActionDefinitionRepoImpl extends AbstractWeaverOrderedRepoImpl<CustomActionDefinition, CustomActionDefinitionRepo> implements CustomActionDefinitionRepoCustom {

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Override
    public CustomActionDefinition create(String label, Boolean isStudentVisible) {
        CustomActionDefinition customActionDefinition = new CustomActionDefinition(label, isStudentVisible);
        customActionDefinition.setPosition(customActionDefinitionRepo.count() + 1);
        return customActionDefinitionRepo.save(customActionDefinition);
    }

    @Override
    public Class<?> getModelClass() {
        return CustomActionDefinition.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/custom-action-definition";
    }

}
