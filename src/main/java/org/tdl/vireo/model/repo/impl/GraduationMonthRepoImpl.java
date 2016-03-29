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
        return graduationMonthRepo.save(new GraduationMonth(month, (int) graduationMonthRepo.count() + 1));
    }
    
    @Override
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(GraduationMonth.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(GraduationMonth.class, column);
    }
    
    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(GraduationMonth.class, index);
    }
    
}
