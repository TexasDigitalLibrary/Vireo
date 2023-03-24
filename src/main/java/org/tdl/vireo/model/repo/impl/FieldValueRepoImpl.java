package org.tdl.vireo.model.repo.impl;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

public class FieldValueRepoImpl extends AbstractWeaverRepoImpl<FieldValue, FieldValueRepo> implements FieldValueRepoCustom {

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Override
    public FieldValue create(FieldPredicate fieldPredicate) {
        return fieldValueRepo.save(new FieldValue(fieldPredicate));
    }

    @Override
    protected String getChannel() {
        return "/channel/field-value";
    }

}
