package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.custom.InputTypeRepoCustom;

public class InputTypeRepoImpl implements InputTypeRepoCustom {

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Override
    public InputType create(String name) {
        return inputTypeRepo.save(new InputType(name));
    }
    
    public InputType create(InputType inputType) {
    	InputType newInputType = create(inputType.getName());
    	newInputType.setValidationPatern(inputType.getValidationPatern());
    	newInputType.setValidationMessage(inputType.getValidationMessage());
        return inputTypeRepo.save(newInputType);
    }

}
