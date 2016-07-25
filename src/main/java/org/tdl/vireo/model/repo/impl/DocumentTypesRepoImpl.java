package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.DocumentTypesRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypesRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class DocumentTypesRepoImpl implements DocumentTypesRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private DocumentTypesRepo documentTypesRepo;
    
    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(DocumentType.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(DocumentType.class, column);
    }
    
    @Override
    public void remove(DocumentType documentType) {
        orderedEntityService.remove(documentTypesRepo, DocumentType.class, documentType.getPosition());
    }

    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel) {
        DocumentType documentType = new DocumentType(name, degreeLevel);
        documentType.setPosition(documentTypesRepo.count() + 1);
        FieldPredicate documentTypePredicate = new FieldPredicate("_docType_" + name, new Boolean(true));
        documentTypePredicate = fieldPredicateRepo.save(documentTypePredicate);
        documentType.setFieldPredicate(documentTypePredicate);
        return documentTypesRepo.save(documentType);
    }
    
}
