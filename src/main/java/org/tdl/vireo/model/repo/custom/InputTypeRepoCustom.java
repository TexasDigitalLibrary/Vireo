package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.InputType;

public interface InputTypeRepoCustom {

    public InputType create(String name);
    
    public InputType create(String name, String patern);

}
