package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.repo.custom.ContactInfoRepoCustom;

public interface ContactInfoRepo extends JpaRepository<ContactInfo, Long>, ContactInfoRepoCustom {

}
