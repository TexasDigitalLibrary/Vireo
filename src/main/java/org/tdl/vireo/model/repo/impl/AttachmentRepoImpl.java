package org.tdl.vireo.model.repo.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.repo.AttachmentRepo;
import org.tdl.vireo.model.repo.custom.AttachmentRepoCustom;

public class AttachmentRepoImpl implements AttachmentRepoCustom {

    @Autowired
    AttachmentRepo attachmentRepo;

    @Override
    public Attachment create(String name, UUID uuid, AttachmentType type) {
        return attachmentRepo.save(new Attachment(name, uuid, type));
    }

}
