package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.repo.AttachmentTypeRepo;
import org.tdl.vireo.model.repo.custom.AttachmentTypeRepoCustom;

public class AttachmentTypeRepoImpl implements AttachmentTypeRepoCustom {
	
	@Autowired 
	AttachmentTypeRepo attachmentTypeRepo;

	@Override
	public AttachmentType create(String name) {
		return attachmentTypeRepo.save(new AttachmentType(name));
	}

}
