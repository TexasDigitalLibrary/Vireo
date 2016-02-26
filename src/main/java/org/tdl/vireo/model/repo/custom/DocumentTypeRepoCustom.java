package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

public interface DocumentTypeRepoCustom {

    public DocumentType create(String name, DegreeLevel degreeLevel);
}
