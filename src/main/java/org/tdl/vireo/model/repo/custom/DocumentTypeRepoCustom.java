package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;

public interface DocumentTypeRepoCustom {

    public DocumentType create(String name);

    public DocumentType create(String name, FieldPredicate fieldPredicate);

}
