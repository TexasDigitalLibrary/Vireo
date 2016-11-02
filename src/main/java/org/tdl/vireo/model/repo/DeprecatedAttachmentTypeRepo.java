package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DeprecatedAttachmentType;
import org.tdl.vireo.model.repo.custom.DeprecatedAttachmentTypeRepoCustom;

public interface DeprecatedAttachmentTypeRepo extends JpaRepository<DeprecatedAttachmentType, Long>, DeprecatedAttachmentTypeRepoCustom {

}
