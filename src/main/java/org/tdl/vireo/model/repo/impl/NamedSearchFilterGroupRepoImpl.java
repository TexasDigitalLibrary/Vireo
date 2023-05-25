package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterGroupRepoCustom;

public class NamedSearchFilterGroupRepoImpl extends AbstractWeaverRepoImpl<NamedSearchFilterGroup, NamedSearchFilterGroupRepo> implements NamedSearchFilterGroupRepoCustom {

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public NamedSearchFilterGroup create(User user) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = createInMemory(user);
        return namedSearchFilterGroupRepo.save(newNamedSearchFilterGroup);
    }

    @Override
    public NamedSearchFilterGroup create(User user, String name) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = createInMemory(user);
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
        NamedSearchFilterGroup newNamedSearchFilterGroup = createInMemory(namedSearchFilterGroup.getUser());
        newNamedSearchFilterGroup.setName(namedSearchFilterGroup.getName());
        return namedSearchFilterGroupRepo.save(clone(newNamedSearchFilterGroup, namedSearchFilterGroup));
    }

    @Override
    public void delete(NamedSearchFilterGroup namedSearchFilterGroup) {
        User user = namedSearchFilterGroup.getUser();

        // If the saved filter active filter being deleted is active filter for any user, then the active filter must be reset for each user.
        List<User> users = userRepo.findAllByActiveFilter(namedSearchFilterGroup);
        for (User userWithFilter : users) {
            // Calling userWithFilter.getSavedFilters().remove(namedSearchFilterGroup) isn't working, so instead explicitly remove by ID.
            for (int i = 0; i < userWithFilter.getSavedFilters().size(); i++) {
                if (userWithFilter.getSavedFilters().get(i).getId() == namedSearchFilterGroup.getId()) {
                    userWithFilter.getSavedFilters().remove(i);
                    break;
                }
            }

            userWithFilter = userRepo.clearActiveFilter(userWithFilter);
            if (userWithFilter.getId() == user.getId()) {
                user = userWithFilter;
            }

            simpMessagingTemplate.convertAndSend("/channel/user/update", new ApiResponse(SUCCESS, userWithFilter));
            simpMessagingTemplate.convertAndSend("/channel/active-filters/" + userWithFilter.getActiveFilter().getId(), new ApiResponse(SUCCESS, userWithFilter.getActiveFilter()));
        }

        // Find all filters for all users associated with the group before deleting the group to detach all filters.
        users = userRepo.findAllBySavedFilters(namedSearchFilterGroup);
        for (User userWithFilter : users) {
            // Calling userWithFilter.getSavedFilters().remove(namedSearchFilterGroup) isn't working, so instead explicitly remove by ID.
            for (int i = 0; i < userWithFilter.getSavedFilters().size(); i++) {
                if (userWithFilter.getSavedFilters().get(i).getId() == namedSearchFilterGroup.getId()) {
                    List<NamedSearchFilter> searchFilters = new ArrayList<>(userWithFilter.getSavedFilters().get(i).getNamedSearchFilters());

                    userWithFilter.getSavedFilters().remove(i);
                    userWithFilter = userRepo.save(userWithFilter);

                    // Do not call deleteAllInBatch() in here because delete() is overridden in namedSearchFilterRepo() and needs to be called.
                    searchFilters.forEach(filter -> {
                        if (namedSearchFilterGroupRepo.countByNamedSearchFilters(filter) == 0) {
                            namedSearchFilterRepo.delete(filter);
                        }
                    });

                    simpMessagingTemplate.convertAndSend("/channel/user/update", new ApiResponse(SUCCESS, userWithFilter));
                    break;
                }
            }
        }

        Set<NamedSearchFilter> namedSearchFilters = namedSearchFilterGroup.getNamedSearchFilters();

        namedSearchFilterGroup.setNamedSearchFilters(null);
        namedSearchFilterGroupRepo.deleteById(namedSearchFilterGroup.getId());

        // The filters directly associated with the filter group should now be deleted if no longer referenced anywhere.
        namedSearchFilters.forEach(filter -> {
            if (namedSearchFilterGroupRepo.countByNamedSearchFilters(filter) == 0) {
                namedSearchFilterRepo.delete(filter);
            }
        });
    }

    private NamedSearchFilterGroup createInMemory(User user) {
        NamedSearchFilterGroup newNamedSearchFilterGroup = new NamedSearchFilterGroup();
        newNamedSearchFilterGroup.setUser(user);
        return newNamedSearchFilterGroup;
    }

    /**
     * Get the persisted filter group for the given user.
     *
     * If no filter exists in the database for the user that is a persisted
     * (non-"saved") filter group, then create a new filter group as a
     * persisted filter. This newly created filter group will be assigned
     *
     * This does not save the user.
     * This does not save the newly created filter group.
     * This does not set the active filter for the user.
     * This does not add any columns or filters to the filter group.
     *
     * @param user The user to get the associated filter of.
     *
     * @return The existing persisted filter group or a newly created filter
     *         group.
     */
    public NamedSearchFilterGroup getOrCreatePersistedActiveFilterForUser(User user) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        // Switch to a not "saved" filter (title is NULL) when clearing.
        List<NamedSearchFilterGroup> filters = namedSearchFilterGroupRepo.findByUserAndNameIsNull(user);

        if (activeFilter == null || activeFilter.getName() != null) {
            if (filters.size() == 0) {
                activeFilter = new NamedSearchFilterGroup();
                activeFilter.setUser(user);
            } else {
                activeFilter = filters.get(0);
            }
        }

        return activeFilter;
    }

    @Override
    protected String getChannel() {
        return "/channel/named-search-filter-group";
    }

}
