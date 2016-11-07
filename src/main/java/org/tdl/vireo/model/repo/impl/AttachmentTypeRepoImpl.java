package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.AttachmentTypeRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.AttachmentTypeRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class AttachmentTypeRepoImpl implements AttachmentTypeRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private AttachmentTypeRepo documentTypeRepo;
    
    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(AttachmentType.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(AttachmentType.class, column);
    }
    
    @Override
    public void remove(AttachmentType documentType) {
        orderedEntityService.remove(documentTypeRepo, AttachmentType.class, documentType.getPosition());
    }

    @Override
    public AttachmentType create(String name) {
        return create(name, fieldPredicateRepo.save(new FieldPredicate("_doctype_" + name.toLowerCase().replace(' ', '_'), new Boolean(true))));
    }
    
    @Override
    public AttachmentType create(String name, FieldPredicate fieldPredicate) {
        AttachmentType documentType = new AttachmentType(name, fieldPredicate);
        documentType.setPosition(documentTypeRepo.count() + 1);
        return documentTypeRepo.save(documentType);
    }
    
}
