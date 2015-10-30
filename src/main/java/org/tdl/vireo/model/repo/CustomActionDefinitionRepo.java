package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.custom.CustomActionDefinitionRepoCustom;

public interface CustomActionDefinitionRepo extends JpaRepository<CustomActionDefinition, Long>, CustomActionDefinitionRepoCustom {

    public CustomActionDefinition findByLabel(String label);

}
