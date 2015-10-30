package org.tdl.vireo.model.repo.custom;

import java.util.UUID;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;

public interface AttachmentRepoCustom {

    public Attachment create(String name, UUID uuid, AttachmentType type);

}
