package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;
import org.tdl.vireo.service.DefaultFiltersService;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

public class UserRepoImpl extends AbstractWeaverRepoImpl<User, UserRepo> implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @Autowired
    private DefaultFiltersService defaultFiltersService;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public User create(String email, String firstName, String lastName, Role role) {
        return saveAndAddSettings(userRepo.create(new User(email, firstName, lastName, role)));
    }

    @Override
    public User create(String email, String firstName, String lastName, String password, Role role) {
        return saveAndAddSettings(userRepo.create(new User(email, firstName, lastName, password, role)));
    }

    private User saveAndAddSettings(User user) {
        initializeActiveFilter(user);

        user.putSetting("id", user.getId().toString());
        user.putSetting("displayName", user.getFirstName() + " " + user.getLastName());
        user.putSetting("preferedEmail", user.getEmail());
        user.setFilterColumns(defaultFiltersService.getDefaultFilter());
        user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

        return userRepo.update(user);
    }

    @Override
    public User create(User user) {
        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/user/create", new ApiResponse(SUCCESS, user));
        return user;
    }

    @Override
    public User update(User user) {
        initializeActiveFilter(user);

        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/user/update", new ApiResponse(SUCCESS, user));
        return user;
    }

    @Override
    public void delete(User user) {
        if (user.getActiveFilter() != null) {
            namedSearchFilterGroupRepo.delete(user.getActiveFilter());
        }

        userRepo.deleteById(user.getId());
        simpMessagingTemplate.convertAndSend("/channel/user/delete", new ApiResponse(SUCCESS));
    }

    @Override
    protected String getChannel() {
        return "/channel/user";
    }

    /**
     * Create an active filter if the user has the appropriate roles.
     *
     * @param user The user to update.
     */
    private void initializeActiveFilter(User user) {
        switch ((Role) user.getRole()) {
            case ROLE_ADMIN:
            case ROLE_MANAGER:
            case ROLE_REVIEWER:
                if (user.getActiveFilter() == null) {
                    user.setActiveFilter(namedSearchFilterGroupRepo.create(user));
                }
                break;
            default:
                break;
        }
    }

}
