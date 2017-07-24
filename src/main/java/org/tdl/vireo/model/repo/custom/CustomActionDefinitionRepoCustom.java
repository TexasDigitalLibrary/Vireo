package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.CustomActionDefinition;

public interface CustomActionDefinitionRepoCustom {

    public CustomActionDefinition create(String label, Boolean isStudentVisible);

    public void reorder(Long src, Long dest);

    public void remove(CustomActionDefinition customActionDefinition);

}
