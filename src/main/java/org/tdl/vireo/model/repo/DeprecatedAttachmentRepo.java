package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.repo.custom.DeprecatedAttachmentRepoCustom;

@Deprecated
public interface DeprecatedAttachmentRepo extends JpaRepository<DeprecatedAttachment, Long>, DeprecatedAttachmentRepoCustom {

}
