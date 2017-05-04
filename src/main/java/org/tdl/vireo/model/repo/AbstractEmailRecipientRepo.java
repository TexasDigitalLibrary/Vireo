package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.AbstractEmailRecipient;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.repo.custom.AbstractEmailRecipientRepoCustom;

public interface AbstractEmailRecipientRepo extends JpaRepository<AbstractEmailRecipient, Long>, AbstractEmailRecipientRepoCustom {}
