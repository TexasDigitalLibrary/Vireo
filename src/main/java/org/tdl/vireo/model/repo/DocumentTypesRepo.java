// package org.tdl.vireo.model.repo;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.tdl.vireo.model.DocumentType;
// import org.tdl.vireo.model.repo.custom.DocumentTypeRepoCustom;

// public interface DocumentTypeRepo extends JpaRepository<DocumentType, Long>, DocumentTypeRepoCustom {    

// }
package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.custom.DocumentTypesRepoCustom;

public interface DocumentTypesRepo extends JpaRepository<DocumentType, Long>, DocumentTypesRepoCustom {
    
    public DocumentType findByName(String name);
    
    public List<DocumentType> findAllByOrderByOrderAsc();
        
        
}
