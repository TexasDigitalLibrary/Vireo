package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

public class DocumentTypeRepoImpl implements DocumentTypeRepoCustom {

    @Autowired
    DocumentTypeRepo documentTypeRepo;

    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel) {
        return documentTypeRepo.save(new DocumentType(name, degreeLevel));
    }
}
