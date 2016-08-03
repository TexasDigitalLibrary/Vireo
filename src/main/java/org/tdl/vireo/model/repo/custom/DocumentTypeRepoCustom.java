package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;

public interface DocumentTypeRepoCustom {

    public DocumentType create(String name, DegreeLevel degreeLevel);
    
    public DocumentType create(String name, DegreeLevel degreeLevel, FieldPredicate fieldPredicate);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);

    public void remove(DocumentType documentType);

}
