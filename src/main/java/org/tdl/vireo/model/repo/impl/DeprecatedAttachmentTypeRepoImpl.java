package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DeprecatedAttachmentType;
import org.tdl.vireo.model.repo.DeprecatedAttachmentTypeRepo;
import org.tdl.vireo.model.repo.custom.DeprecatedAttachmentTypeRepoCustom;

@Deprecated
public class DeprecatedAttachmentTypeRepoImpl implements DeprecatedAttachmentTypeRepoCustom {

    @Autowired
    DeprecatedAttachmentTypeRepo attachmentTypeRepo;

    @Override
    public DeprecatedAttachmentType create(String name) {
        return attachmentTypeRepo.save(new DeprecatedAttachmentType(name));
    }

}
