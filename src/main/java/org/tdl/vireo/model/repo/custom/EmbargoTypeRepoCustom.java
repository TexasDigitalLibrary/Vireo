package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmbargoType;

public interface EmbargoTypeRepoCustom {
    public EmbargoType create(String name, String description, Integer duration);
}
