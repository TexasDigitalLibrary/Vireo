package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class DocumentTypeRepoImpl extends AbstractWeaverOrderedRepoImpl<DocumentType, DocumentTypeRepo> implements DocumentTypeRepoCustom {

    @Autowired
    private DocumentTypeRepo documentTypeRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Override
    public DocumentType create(String name) {
        return create(name, fieldPredicateRepo.save(new FieldPredicate("_doctype_" + name.toLowerCase().replace(' ', '_'), new Boolean(true))));
    }

    @Override
    public DocumentType create(String name, FieldPredicate fieldPredicate) {
        DocumentType documentType = new DocumentType(name, fieldPredicate);
        documentType.setPosition(documentTypeRepo.count() + 1);
        return documentTypeRepo.save(documentType);
    }

    @Override
    public Class<?> getModelClass() {
        return DocumentType.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/document-type";
    }

}
