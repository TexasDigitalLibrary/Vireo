package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.custom.FieldPredicateRepoCustom;

public class FieldPredicateRepoImpl implements FieldPredicateRepoCustom {

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Override
    public FieldPredicate create(String value) {
        return fieldPredicateRepo.save(new FieldPredicate(value));
    }
}
