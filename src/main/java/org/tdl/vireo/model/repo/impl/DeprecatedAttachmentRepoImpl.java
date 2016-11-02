package org.tdl.vireo.model.repo.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.DeprecatedAttachmentType;
import org.tdl.vireo.model.repo.DeprecatedAttachmentRepo;
import org.tdl.vireo.model.repo.custom.DeprecatedAttachmentRepoCustom;

public class DeprecatedAttachmentRepoImpl implements DeprecatedAttachmentRepoCustom {

    @Autowired
    DeprecatedAttachmentRepo attachmentRepo;

    @Override
    public DeprecatedAttachment create(String name, UUID uuid, DeprecatedAttachmentType type) {
        return attachmentRepo.save(new DeprecatedAttachment(name, uuid, type));
    }

}
