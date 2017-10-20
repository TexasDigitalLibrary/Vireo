package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface DocumentTypeRepo extends WeaverOrderedRepo<DocumentType>, DocumentTypeRepoCustom {

    public DocumentType findByName(String name);

    public DocumentType findByNameAndFieldPredicate(String name, FieldPredicate fieldPredicate);

}
