package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
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
    private NamedSearchFilterRepo namedSearchFilterRepo;

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

    /**
     * Clear the active filter group for the given user, creating a persisted filer group if necessary.
     *
     * This removes existing filters and columns on the persisted filter group (aka not-"saved" filter group).
     *
     * This does not send a message on channel "/channel/user/update".
     *
     * @param user The user to clear the active filter group of.
     *
     * @return The updated user model.
     */
    public User clearActiveFilter(User user) {
        NamedSearchFilterGroup persistFilterGroup = namedSearchFilterGroupRepo.getOrCreatePersistedActiveFilterForUser(user);
        List<NamedSearchFilter> searchFilters = new ArrayList<>(persistFilterGroup.getNamedSearchFilters());

        persistFilterGroup.getNamedSearchFilters().clear();
        persistFilterGroup.getSavedColumns().clear();
        persistFilterGroup.setColumnsFlag(false);

        persistFilterGroup = namedSearchFilterGroupRepo.save(persistFilterGroup);

        user.setActiveFilter(persistFilterGroup);
        user = userRepo.save(user);

        // Do not call deleteAllInBatch() in here because delete() is overridden in namedSearchFilterRepo() and delete() must be called.
        searchFilters.forEach(filter -> {
            namedSearchFilterRepo.delete(filter);
        });

        return user;
    }

    /**
     * Set the active filter group.
     *
     * This copies the active filter if it is a saved filter to isolate it from the saved filter.
     *
     * @param user The user to update the active filter group of.
     * @param filter The active filter group to set.
     *
     * @return The updated user model.
     */
    public User setActiveFilter(User user, NamedSearchFilterGroup filter) {

        if (filter.getName() == null) {
            user.setActiveFilter(filter);
            return userRepo.update(user);
        }

        NamedSearchFilterGroup persistedFilterGroup = namedSearchFilterGroupRepo.getOrCreatePersistedActiveFilterForUser(user);

        persistedFilterGroup.getNamedSearchFilters().clear();
        persistedFilterGroup.getSavedColumns().clear();
        persistedFilterGroup.setColumnsFlag(persistedFilterGroup.getColumnsFlag());

        for (NamedSearchFilter namedSearchFilter : filter.getNamedSearchFilters()) {
            persistedFilterGroup.addFilterCriterion(namedSearchFilterRepo.clone(namedSearchFilter));
        };

        if (filter.getColumnsFlag()) {
            for (SubmissionListColumn column : filter.getSavedColumns()) {
                persistedFilterGroup.addSavedColumn(column);
            }
        }

        persistedFilterGroup = namedSearchFilterGroupRepo.save(persistedFilterGroup);

        user.setActiveFilter(persistedFilterGroup);
        return userRepo.update(user);
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
        for (NamedSearchFilterGroup namedSearchFilterGroup : namedSearchFilterGroupRepo.findByUser(user)) {
            namedSearchFilterGroupRepo.delete(namedSearchFilterGroup);
        }

        user.setActiveFilter(null);
        user.setSavedFilters(null);
        super.delete(user);

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
