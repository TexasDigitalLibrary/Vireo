package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

public interface DocumentTypeRepo extends JpaRepository<DocumentType, Long>, DocumentTypeRepoCustom {    

}
