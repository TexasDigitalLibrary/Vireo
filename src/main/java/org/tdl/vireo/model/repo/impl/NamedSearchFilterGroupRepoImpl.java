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
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Autowired
    private NamedSearchFilterRepo namedSerachFilterRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public NamedSearchFilterGroup create(User user) {
        NamedSearchFilterGroup newNamedSearchFilter = new NamedSearchFilterGroup();
        newNamedSearchFilter.setUser(user);

        return namedSearchFilterRepo.save(newNamedSearchFilter);
    }

    @Override
    public NamedSearchFilterGroup create(User user, String name) {
        NamedSearchFilterGroup newNamedSearchFilter = new NamedSearchFilterGroup();
        newNamedSearchFilter.setUser(user);
        newNamedSearchFilter.setName(name);
        return namedSearchFilterRepo.save(newNamedSearchFilter);
    }

    public NamedSearchFilterGroup clone(NamedSearchFilterGroup newNamedSearchFilterGroup, NamedSearchFilterGroup namedSearchFilterGroup) {
        newNamedSearchFilterGroup.setPublicFlag(namedSearchFilterGroup.getPublicFlag());
        newNamedSearchFilterGroup.setUmiRelease(namedSearchFilterGroup.getUmiRelease());
        newNamedSearchFilterGroup.setColumnsFlag(namedSearchFilterGroup.getColumnsFlag());
        namedSearchFilterGroup.getNamedSearchFilters().forEach(filterCriterion -> {
            newNamedSearchFilterGroup.addFilterCriterion(namedSerachFilterRepo.cloneFilterCriterion(filterCriterion));
        });

        if (newNamedSearchFilterGroup.getColumnsFlag()) {
            namedSearchFilterGroup.getSavedColumns().forEach(column -> {
                newNamedSearchFilterGroup.addSavedColumn(column);
            });
        }

        return newNamedSearchFilterGroup;
    }

    public NamedSearchFilterGroup createFromFilter(NamedSearchFilterGroup namedSearchFilterGroup) {
        NamedSearchFilterGroup newNamedSearchFilter = namedSearchFilterRepo.create(namedSearchFilterGroup.getUser());
        newNamedSearchFilter.setName(namedSearchFilterGroup.getName());

        return namedSearchFilterRepo.save(clone(newNamedSearchFilter, namedSearchFilterGroup));
    }

    @Override
    public void delete(NamedSearchFilterGroup namedSearchFilterGroup) {
        User user = namedSearchFilterGroup.getUser();
        user.setActiveFilter(null);
        userRepo.save(user);
        namedSearchFilterGroup.setUser(null);
        namedSearchFilterGroup.setNamedSearchFilters(null);
        namedSearchFilterGroup.setSavedColumns(null);
        namedSearchFilterRepo.delete(namedSearchFilterGroup.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/named-search-filter-group";
    }

}
