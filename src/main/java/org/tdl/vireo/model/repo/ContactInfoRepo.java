package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.repo.custom.ContactInfoRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ContactInfoRepo extends WeaverRepo<ContactInfo>, ContactInfoRepoCustom {

}
