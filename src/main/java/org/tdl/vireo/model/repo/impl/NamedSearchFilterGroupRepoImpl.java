package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterGroupRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class NamedSearchFilterGroupRepoImpl extends AbstractWeaverRepoImpl<NamedSearchFilterGroup, NamedSearchFilterGroupRepo> implements NamedSearchFilterGroupRepoCustom {

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public NamedSearchFilterGroup create(User user) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = new NamedSearchFilterGroup();
        newNamedSearchFilterGroup.setUser(user);
        return namedSearchFilterGroupRepo.save(newNamedSearchFilterGroup);
    }

    @Override
    public NamedSearchFilterGroup create(User user, String name) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = new NamedSearchFilterGroup();
        newNamedSearchFilterGroup.setUser(user);
        newNamedSearchFilterGroup.setName(name);
        return namedSearchFilterGroupRepo.save(newNamedSearchFilterGroup);
    }

    public NamedSearchFilterGroup clone(NamedSearchFilterGroup newNamedSearchFilterGroup, NamedSearchFilterGroup namedSearchFilterGroup) {
        newNamedSearchFilterGroup.setPublicFlag(namedSearchFilterGroup.getPublicFlag());
        newNamedSearchFilterGroup.setUmiRelease(namedSearchFilterGroup.getUmiRelease());
        newNamedSearchFilterGroup.setColumnsFlag(namedSearchFilterGroup.getColumnsFlag());
        namedSearchFilterGroup.getNamedSearchFilters().forEach(namedSearchFilter -> {
            newNamedSearchFilterGroup.addFilterCriterion(namedSearchFilterRepo.clone(namedSearchFilter));
        });
        if (newNamedSearchFilterGroup.getColumnsFlag()) {
            namedSearchFilterGroup.getSavedColumns().forEach(column -> {
                newNamedSearchFilterGroup.addSavedColumn(column);
            });
        }
        return newNamedSearchFilterGroup;
    }

    public NamedSearchFilterGroup createFromFilter(NamedSearchFilterGroup namedSearchFilterGroup) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = namedSearchFilterGroupRepo.create(namedSearchFilterGroup.getUser());
        newNamedSearchFilterGroup.setName(namedSearchFilterGroup.getName());
        return namedSearchFilterGroupRepo.save(clone(newNamedSearchFilterGroup, namedSearchFilterGroup));
    }

    @Override
    public void delete(NamedSearchFilterGroup namedSearchFilterGroup) {
        User user = namedSearchFilterGroup.getUser();
        user.setActiveFilter(null);
        userRepo.save(user);
        namedSearchFilterGroup.setUser(null);
        namedSearchFilterGroup.setNamedSearchFilters(null);
        namedSearchFilterGroup.setSavedColumns(null);
        namedSearchFilterGroupRepo.delete(namedSearchFilterGroup.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/named-search-filter-group";
    }

}
