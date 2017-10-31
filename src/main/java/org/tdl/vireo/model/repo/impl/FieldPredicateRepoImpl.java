package org.tdl.vireo.model.repo.impl;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class FieldPredicateRepoImpl extends AbstractWeaverRepoImpl<FieldPredicate, FieldPredicateRepo> implements FieldPredicateRepoCustom {

    @Override
    public FieldPredicate create(String value, Boolean documentTypePredicate) {
        return super.create(new FieldPredicate(value, documentTypePredicate));
    }

    @Override
    protected String getChannel() {
        return "/channel/field-predicate";
    }

}
