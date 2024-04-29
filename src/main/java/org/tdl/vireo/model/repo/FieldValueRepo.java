package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

public interface FieldValueRepo extends WeaverRepo<FieldValue>, FieldValueRepoCustom {

}
