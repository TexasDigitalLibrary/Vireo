package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.custom.CustomActionDefinitionRepoCustom;

public class CustomActionDefinitionRepoImpl implements CustomActionDefinitionRepoCustom {

    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;

    @Override
    public CustomActionDefinition create(String label, Boolean isStudentVisible) {
        return customActionDefinitionRepo.save(new CustomActionDefinition(label, isStudentVisible));
    }

}
