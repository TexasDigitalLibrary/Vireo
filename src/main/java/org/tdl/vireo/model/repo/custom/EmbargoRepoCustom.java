package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Embargo;

public interface EmbargoRepoCustom {
    
    public Embargo create(String name, String description, Integer duration, boolean isActive);

}
