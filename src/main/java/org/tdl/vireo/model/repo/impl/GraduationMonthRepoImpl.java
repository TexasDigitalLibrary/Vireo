package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class GraduationMonthRepoImpl extends AbstractWeaverOrderedRepoImpl<GraduationMonth, GraduationMonthRepo> implements GraduationMonthRepoCustom {

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Override
    public GraduationMonth create(int month) {
        GraduationMonth graduationMonth = new GraduationMonth(month);
        graduationMonth.setPosition(graduationMonthRepo.count() + 1);
        return super.create(graduationMonth);
    }

    @Override
    public Class<?> getModelClass() {
        return GraduationMonth.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/graduation-month";
    }

}
