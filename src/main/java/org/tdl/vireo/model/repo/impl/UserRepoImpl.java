package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;
import org.tdl.vireo.service.DefaultFiltersService;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class UserRepoImpl extends AbstractWeaverRepoImpl<User, UserRepo> implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Autowired
    private DefaultFiltersService defaultFiltersService;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @Override
    public User create(String email, String firstName, String lastName, Role role) {

        User newUser = new User(email, firstName, lastName, role);

        newUser = userRepo.save(newUser);

        NamedSearchFilterGroup activeFilter = namedSearchFilterRepo.create(newUser);

        newUser.putSetting("id", newUser.getId().toString());
        newUser.putSetting("displayName", newUser.getFirstName() + " " + newUser.getLastName());
        newUser.putSetting("preferedEmail", newUser.getEmail());
        newUser.setActiveFilter(activeFilter);
        newUser.setFilterColumns(defaultFiltersService.getDefaultFilter());
        newUser.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

        return userRepo.save(newUser);
    }

    @Override
    public void delete(User user) {
        namedSearchFilterRepo.delete(user.getActiveFilter());
        userRepo.delete(user.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/user";
    }

}
