package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;

public class GraduationMonthRepoImpl implements GraduationMonthRepoCustom {

    @Autowired
    GraduationMonthRepo graduationMonthRepo;

    @Override
    public GraduationMonth create(int month) {
        return graduationMonthRepo.save(new GraduationMonth(month)); 
    }
}
