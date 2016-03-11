package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypesRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypesRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class DocumentTypesRepoImpl implements DocumentTypesRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private DocumentTypesRepo documentTypesRepo;
    
    @Override
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(DocumentType.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(DocumentType.class, column);
    }
    
    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(DocumentType.class, index);
    }

    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel) {
        return documentTypesRepo.save(new DocumentType(name, degreeLevel, (int)documentTypesRepo.count() + 1));
    }
    
}
