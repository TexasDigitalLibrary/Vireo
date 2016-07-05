package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
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
        return documentTypesRepo.save(documentType);
    }
    
    @Override
    public DocumentType validateCreate(DocumentType documentType) {
        DocumentType existing = documentTypesRepo.findByNameAndDegreeLevel(documentType.getName(), documentType.getDegreeLevel());
        if(!documentType.getBindingResult().hasErrors() && existing != null){
            documentType.getBindingResult().addError(new ObjectError("documentType", documentType.getName() + " with degree level " + documentType.getDegreeLevel() + " is already a document type!"));
        }
        
        return documentType;
    }
    
    @Override
    public DocumentType validateUpdate(DocumentType documentType) {
        if(documentType.getId() == null) {
            documentType.getBindingResult().addError(new ObjectError("documentType", "Cannot update a DocumentType without an id!"));
        } else {
            DocumentType documentTypeToUpdate = documentTypesRepo.findOne(documentType.getId());
            if(documentTypeToUpdate == null) {
                documentType.getBindingResult().addError(new ObjectError("documentType", "Cannot update a DocumentType with an invalid id!"));
            } else {
                documentTypeToUpdate.setBindingResult(documentType.getBindingResult());
                documentTypeToUpdate.setName(documentType.getName());        
                documentTypeToUpdate.setDegreeLevel(documentType.getDegreeLevel());
                documentType = documentTypeToUpdate;
            }
        }
        
        return documentType;
    }

}
