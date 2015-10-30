package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.repo.custom.AttachmentRepoCustom;

public interface AttachmentRepo extends JpaRepository<Attachment, Long>, AttachmentRepoCustom {
	
}
