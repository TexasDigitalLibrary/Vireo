package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.repo.custom.AttachmentTypeRepoCustom;

public interface AttachmentTypeRepo extends JpaRepository<AttachmentType, Long>, AttachmentTypeRepoCustom {
	
}
