package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.custom.CustomActionDefinitionRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface CustomActionDefinitionRepo extends WeaverOrderedRepo<CustomActionDefinition>, CustomActionDefinitionRepoCustom {

    public CustomActionDefinition findByLabel(String label);
}
