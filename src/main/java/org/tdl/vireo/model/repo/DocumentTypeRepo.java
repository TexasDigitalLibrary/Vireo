package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

public interface DocumentTypeRepo extends JpaRepository<DocumentType, Long>, DocumentTypeRepoCustom {

    public DocumentType findByName(String name);

    public DocumentType findByNameAndFieldPredicate(String name, FieldPredicate fieldPredicate);

    public List<DocumentType> findAllByOrderByPositionAsc();

}
