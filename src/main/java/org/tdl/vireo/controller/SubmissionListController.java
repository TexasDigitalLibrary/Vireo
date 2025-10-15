package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FilterAction;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/submission-list")
public class SubmissionListController {

    private final static Logger LOG = LoggerFactory.getLogger(SubmissionListController.class);

    private final static String SEARCH_BOX_TITLE = "Search Box";

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionListColumnService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FilterCriterionRepo filterCriterionRepo;

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @RequestMapping("/all-columns")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getSubmissionViewColumns() {
        return new ApiResponse(SUCCESS, submissionListColumnRepo.findAll());
    }

    @RequestMapping("/columns-by-user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getSubmissionViewColumnsByUser(@WeaverUser User user) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        return new ApiResponse(SUCCESS, activeFilter.getColumnsFlag() ? activeFilter.getSavedColumns() : user.getSubmissionViewColumns());
    }

    @RequestMapping("/filter-columns-by-user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getFilterColumnsByUser(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @RequestMapping("/pagesize-by-user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getSubmissionViewPageSizeByUser(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, user.getPageSize());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/update-user-columns/{pageSize}", method = POST)
    public ApiResponse updateUserSubmissionViewColumns(@WeaverUser User user, @PathVariable Integer pageSize, @RequestBody List<SubmissionListColumn> submissionViewColumns) {
        clearColumnCreate(user);
        user.setPageSize(pageSize);
        user.setSubmissionViewColumns(submissionViewColumns);
        user = userRepo.update(user);
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    @RequestMapping("/reset-user-columns")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse resetUserSubmissionViewColumns(@WeaverUser User user) {
        clearColumnCreate(user);
        user.setPageSize(10);
        user.setSubmissionViewColumns(defaultSubmissionListColumnService.getDefaultSubmissionListColumns());
        user = userRepo.update(user);
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    private void clearColumnCreate(User user) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        if (activeFilter.getColumnsFlag()) {
            NamedSearchFilterGroup newActiveFilter = namedSearchFilterGroupRepo.create(user);
            newActiveFilter.setUmiRelease(activeFilter.getUmiRelease());
            activeFilter.getNamedSearchFilters().forEach(namedSearchFilter -> {
                newActiveFilter.addFilterCriterion(namedSearchFilterRepo.clone(namedSearchFilter));
            });
            user.setActiveFilter(newActiveFilter);
        }
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/update-user-filter-columns", method = POST)
    public ApiResponse updateUserFilterColumns(@WeaverUser User user, @RequestBody List<SubmissionListColumn> filterColumns) {
        user.setFilterColumns(filterColumns);
        user = userRepo.update(user);
        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/update-sort", method = POST)
    public ApiResponse updateSort(@WeaverUser User user, @RequestBody List<SubmissionListColumn> submissionViewColumns) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        for (SubmissionListColumn submissionViewColumn : submissionViewColumns) {
            if (submissionViewColumn.getSortOrder() == 1) {
                activeFilter.setSortColumnTitle(submissionViewColumn.getTitle());
                activeFilter.setSortDirection(submissionViewColumn.getSort());
                break;
            }
        }

        activeFilter = namedSearchFilterGroupRepo.update(activeFilter);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.SORT, user.getActiveFilter()));

        return new ApiResponse(SUCCESS, activeFilter);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/set-active-filter", method = POST)
    public ApiResponse setActiveFilter(@WeaverUser User user, @RequestBody NamedSearchFilterGroup namedSearchFilterGroup) {

        Optional<NamedSearchFilterGroup> desiredFilter = namedSearchFilterGroupRepo.findById(namedSearchFilterGroup.getId());

        if (desiredFilter.isEmpty()) {
            return new ApiResponse(ERROR, "Failed to find filter with ID " + namedSearchFilterGroup.getId() + ".");
        }

        user = userRepo.setActiveFilter(user, desiredFilter.get());

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.SET, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/active-filters")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getActiveFilters(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @RequestMapping("/saved-filters")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getSavedFilters(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/remove-saved-filter", method = POST)
    public ApiResponse removeSavedFilter(@WeaverUser User user, @WeaverValidatedModel NamedSearchFilterGroup savedFilter) {
        Optional<NamedSearchFilterGroup> filterGroup = namedSearchFilterGroupRepo.findById(savedFilter.getId());

        if (filterGroup.isEmpty()) {
            return new ApiResponse(ERROR, "Cannot not find filter with ID " + savedFilter.getId() + ".");
        }

        if (filterGroup.get().getUser().getId() != user.getId()) {
            return new ApiResponse(ERROR, "Cannot delete filter, you do not own filter with ID " + filterGroup.get().getId() + ".");
        }

        Boolean isPublic = filterGroup.get().getPublicFlag();
        Long fgId = filterGroup.get().getId();

        namedSearchFilterGroupRepo.delete(filterGroup.get());

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getActiveFilter()));
        simpMessagingTemplate.convertAndSend("/channel/saved-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getSavedFilters()));

        if (isPublic == true) {
            simpMessagingTemplate.convertAndSend("/channel/saved-filters/public", new ApiResponse(SUCCESS, FilterAction.REMOVE, fgId));
        }

        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/add-filter-criterion", method = POST)
    public ApiResponse addFilterCriterion(@WeaverUser User user, @RequestBody Map<String, Object> data) {

        String criterionName = (String) data.get("criterionName");
        String filterValue = String.valueOf(data.get("filterValue"));
        Boolean exactMatch = (Boolean) data.get("exactMatch");
        String filterGloss = (String) data.get("filterGloss");

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        NamedSearchFilter namedSearchFilter = null;

        for (NamedSearchFilter criterion : activeFilter.getNamedSearchFilters()) {
            if (criterion.getName().equals(criterionName)) {
                namedSearchFilter = criterion;
                break;
            }
        }

        if (namedSearchFilter == null) {
            SubmissionListColumn column = submissionListColumnRepo.findByTitle(criterionName);
            for (NamedSearchFilter existingFilter : activeFilter.getNamedSearchFilters()) {
                if (existingFilter.getSubmissionListColumn() != null &&
                    existingFilter.getSubmissionListColumn().getPredicate() != null &&
                    existingFilter.getSubmissionListColumn().getPredicate().equals(column.getPredicate()) &&
                    existingFilter.getSubmissionListColumn().getValuePath().equals(column.getValuePath())) {
                    namedSearchFilter = existingFilter;
                    break;
                }
            }

            if (namedSearchFilter == null) {
                namedSearchFilter = namedSearchFilterRepo.create(column);
            }
        }

        namedSearchFilter.addFilter(filterCriterionRepo.create(filterValue, filterGloss));

        namedSearchFilter.setExactMatch(exactMatch);

        namedSearchFilter.setAllColumnSearch(criterionName.equals(SEARCH_BOX_TITLE) ? true : false);

        activeFilter.addFilterCriterion(namedSearchFilter);

        user = userRepo.update(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/remove-filter-criterion/{namedSearchFilterName}", method = POST)
    public ApiResponse removeFilterCriterion(@WeaverUser User user, @PathVariable String namedSearchFilterName, @RequestBody FilterCriterion filterCriterion) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        String filterValue = null;
        for (NamedSearchFilter namedSearchFilter : activeFilter.getNamedSearchFilters()) {
            if (namedSearchFilter.getName().equals(namedSearchFilterName)) {
                for (FilterCriterion fc : namedSearchFilter.getFilters()) {
                    filterValue = fc.getValue();
                    if (filterValue == null) {
                        if (filterCriterion.getValue() == null && fc.getGloss().equals(filterCriterion.getGloss())) {
                            namedSearchFilter.removeFilter(fc);
                            if (namedSearchFilter.getFilters().size() == 0) {
                                activeFilter.removeNamedSearchFilter(namedSearchFilter);
                            }
                            break;
                        }
                    } else if (filterValue.equals(filterCriterion.getValue()) && fc.getGloss().equals(filterCriterion.getGloss())) {
                        namedSearchFilter.removeFilter(fc);
                        if (namedSearchFilter.getFilters().size() == 0) {
                            activeFilter.removeNamedSearchFilter(namedSearchFilter);
                        }
                        break;
                    }
                }
                break;
            }
        }

        user = userRepo.update(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/clear-filter-criteria")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse clearFilterCriteria(@WeaverUser User user) {
        user = userRepo.clearActiveFilter(user);

        simpMessagingTemplate.convertAndSend("/channel/user/update", new ApiResponse(SUCCESS, user));
        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.CLEAR, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/all-saved-filter-criteria")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getAllSaveFilterCriteria(@WeaverUser User user) {
        List<NamedSearchFilterGroup> all = namedSearchFilterGroupRepo.findByUserIsNotAndPublicFlagTrue(user);
        all.addAll(user.getSavedFilters());

        return new ApiResponse(SUCCESS, all);
    }

    @RequestMapping(value = "/save-filter-criteria", method = POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse saveFilterCriteria(@WeaverUser User user, @WeaverValidatedModel NamedSearchFilterGroup namedSearchFilterGroup) {

        NamedSearchFilterGroup existingFilter = namedSearchFilterGroupRepo.findByNameAndPublicFlagTrue(namedSearchFilterGroup.getName());
        boolean wasPublic = false;

        if (existingFilter != null) {
            if (existingFilter.getUser() == null || existingFilter.getUser().getId() != user.getId()) {
                return new ApiResponse(ERROR, "Cannot save filter because a public filter with the name '" + existingFilter.getName() + "' already exists.");
            }

            wasPublic = existingFilter.getPublicFlag();
            namedSearchFilterGroupRepo.clone(existingFilter, namedSearchFilterGroup);
        } else {
            boolean foundFilter = false;

            for (NamedSearchFilterGroup filter : user.getSavedFilters()) {
                if (filter.getName().equals(namedSearchFilterGroup.getName())) {
                    filter.getNamedSearchFilters().clear();
                    existingFilter = namedSearchFilterGroupRepo.clone(filter, namedSearchFilterGroup);
                    foundFilter = true;
                    break;
                }
            }

            if (!foundFilter) {
                namedSearchFilterGroup.setUser(user);
                existingFilter = namedSearchFilterGroupRepo.createFromFilter(namedSearchFilterGroup);
                user.getSavedFilters().add(existingFilter);
            }
        }

        userRepo.update(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getActiveFilter()));
        simpMessagingTemplate.convertAndSend("/channel/saved-filters/user/" + user.getId(), new ApiResponse(SUCCESS, FilterAction.REFRESH, user.getSavedFilters()));

        if (existingFilter != null && existingFilter.getPublicFlag() == true || wasPublic) {
            simpMessagingTemplate.convertAndSend("/channel/saved-filters/public", new ApiResponse(SUCCESS, FilterAction.SAVE, existingFilter));
        }

        return new ApiResponse(SUCCESS);
    }

}
