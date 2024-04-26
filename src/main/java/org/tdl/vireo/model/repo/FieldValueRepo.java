package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;

public interface FieldValueRepo extends WeaverRepo<FieldValue>, FieldValueRepoCustom {

    public List<FieldValue> findAllByFieldPredicate(FieldPredicate fieldPredicate);

}
