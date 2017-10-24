package org.tdl.vireo.model.repo.impl;

import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.custom.InputTypeRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class InputTypeRepoImpl extends AbstractWeaverRepoImpl<InputType, InputTypeRepo> implements InputTypeRepoCustom {

    @Override
    public InputType create(String name) {
        return super.create(new InputType(name));
    }

    @Override
    protected String getChannel() {
        return "/channel/input-type";
    }

}
