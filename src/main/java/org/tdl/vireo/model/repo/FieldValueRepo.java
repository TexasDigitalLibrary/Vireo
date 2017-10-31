package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface FieldValueRepo extends WeaverRepo<FieldValue>, FieldValueRepoCustom {

}
