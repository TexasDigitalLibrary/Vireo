package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;

public interface DocumentTypeRepoCustom {

    public DocumentType create(String name);

    public DocumentType create(String name, FieldPredicate fieldPredicate);

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(DocumentType documentType);

}
