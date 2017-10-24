package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.repo.custom.InputTypeRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface InputTypeRepo extends WeaverRepo<InputType>, InputTypeRepoCustom {

    public InputType findByName(String name);

}
