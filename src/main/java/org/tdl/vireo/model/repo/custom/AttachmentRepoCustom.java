package org.tdl.vireo.model.repo.custom;

import java.util.UUID;

import org.tdl.vireo.model.Attachment;

public interface AttachmentRepoCustom {
	public Attachment create(String name, UUID uuid);
}
