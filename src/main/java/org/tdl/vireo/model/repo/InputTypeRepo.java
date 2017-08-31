package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.repo.custom.InputTypeRepoCustom;

public interface InputTypeRepo extends JpaRepository<InputType, Long>, InputTypeRepoCustom {

	// Name is not a unique constraint of input type, this method might fail
    public InputType findByName(String name);

}
