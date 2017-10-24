package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.AbstractEmailRecipient;
import org.tdl.vireo.model.repo.custom.AbstractEmailRecipientRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface AbstractEmailRecipientRepo extends WeaverRepo<AbstractEmailRecipient>, AbstractEmailRecipientRepoCustom {

}
