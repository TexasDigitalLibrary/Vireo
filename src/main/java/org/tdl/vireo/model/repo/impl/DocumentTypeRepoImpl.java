package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class DocumentTypeRepoImpl implements DocumentTypeRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private DocumentTypeRepo documentTypeRepo;
    
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
        orderedEntityService.remove(documentTypeRepo, DocumentType.class, documentType.getPosition());
    }

    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel) {
        return create(name, degreeLevel, fieldPredicateRepo.save(new FieldPredicate("_doctype_" + name.toLowerCase().replace(' ', '_'), new Boolean(true))));
    }
    
    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel, FieldPredicate fieldPredicate) {
        DocumentType documentType = new DocumentType(name, degreeLevel, fieldPredicate);
        documentType.setPosition(documentTypeRepo.count() + 1);
        return documentTypeRepo.save(documentType);
    }
    
}
