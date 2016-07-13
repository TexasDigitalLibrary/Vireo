package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class GraduationMonthRepoImpl implements GraduationMonthRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private GraduationMonthRepo graduationMonthRepo;
    
    @Override
    public GraduationMonth create(int month) {
        GraduationMonth graduationMonth = new GraduationMonth(month);
        graduationMonth.setPosition(graduationMonthRepo.count() + 1);
        return graduationMonthRepo.save(graduationMonth);
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(GraduationMonth.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(GraduationMonth.class, column);
    }
    
    @Override
    public void remove(GraduationMonth graduationMonth) {
        orderedEntityService.remove(graduationMonthRepo, GraduationMonth.class, graduationMonth.getPosition());
    }
    
}
