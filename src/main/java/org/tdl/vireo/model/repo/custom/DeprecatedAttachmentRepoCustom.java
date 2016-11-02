package org.tdl.vireo.model.repo.custom;

import java.util.UUID;

import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.DeprecatedAttachmentType;

public interface DeprecatedAttachmentRepoCustom {

    public DeprecatedAttachment create(String name, UUID uuid, DeprecatedAttachmentType type);

}
