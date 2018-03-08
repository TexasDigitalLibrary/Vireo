package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    @RequestMapping("/reset-user-columns")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse resetUserSubmissionViewColumns(@WeaverUser User user) {
        clearColumnCreate(user);
        user.setPageSize(10);
        user.setSubmissionViewColumns(defaultSubmissionListColumnService.getDefaultSubmissionListColumns());
        user = userRepo.save(user);
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
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/set-active-filter", method = POST)
    public ApiResponse setActiveFilter(@WeaverUser User user, @RequestBody NamedSearchFilterGroup namedSearchFilterGroup) {

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        activeFilter.getNamedSearchFilters().clear();
        activeFilter.getSavedColumns().clear();

        activeFilter = namedSearchFilterGroupRepo.clone(activeFilter, namedSearchFilterGroup);

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + activeFilter.getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

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
        user.getSavedFilters().remove(savedFilter);
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/add-filter-criterion", method = POST)
    public ApiResponse addFilterCriterion(@WeaverUser User user, @RequestBody Map<String, Object> data) {

        String criterionName = (String) data.get("criterionName");
        String filterValue = (String) data.get("filterValue");
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
            namedSearchFilter = namedSearchFilterRepo.create(submissionListColumnRepo.findByTitle(criterionName));
        }

        namedSearchFilter.addFilter(filterCriterionRepo.create(filterValue, filterGloss));

        namedSearchFilter.setExactMatch(exactMatch);

        namedSearchFilter.setAllColumnSearch(criterionName.equals(SEARCH_BOX_TITLE) ? true : false);

        activeFilter.addFilterCriterion(namedSearchFilter);

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping(value = "/remove-filter-criterion/{namedSearchFilterName}", method = POST)
    public ApiResponse removeFilterCriterion(@WeaverUser User user, @PathVariable String namedSearchFilterName, @RequestBody FilterCriterion filterCriterion) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        for (NamedSearchFilter namedSearchFilter : activeFilter.getNamedSearchFilters()) {
            if (namedSearchFilter.getName().equals(namedSearchFilterName)) {
                for (FilterCriterion fc : namedSearchFilter.getFilters()) {
                    if (fc.getValue().equals(filterCriterion.getValue()) && fc.getGloss().equals(filterCriterion.getGloss())) {
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

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/clear-filter-criteria")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse clearFilterCriteria(@WeaverUser User user) {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        activeFilter.getNamedSearchFilters().clear();
        activeFilter.getSavedColumns().clear();
        activeFilter.setColumnsFlag(false);

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + activeFilter.getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/all-saved-filter-criteria")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getAllSaveFilterCriteria(@WeaverUser User user) {
        List<NamedSearchFilterGroup> userSavedFilters = user.getSavedFilters();
        List<NamedSearchFilterGroup> publicSavedFilters = namedSearchFilterGroupRepo.findByPublicFlagTrue();

        List<NamedSearchFilterGroup> allSavedFilters = new ArrayList<NamedSearchFilterGroup>();
        allSavedFilters.addAll(userSavedFilters);
        allSavedFilters.addAll(publicSavedFilters);

        return new ApiResponse(SUCCESS, userSavedFilters);
    }

    @RequestMapping(value = "/save-filter-criteria", method = POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse saveFilterCriteria(@WeaverUser User user, @WeaverValidatedModel NamedSearchFilterGroup namedSearchFilterGroup) {

        NamedSearchFilterGroup existingFilter = namedSearchFilterGroupRepo.findByNameAndPublicFlagTrue(namedSearchFilterGroup.getName());

        if (existingFilter != null) {
            existingFilter = namedSearchFilterGroupRepo.clone(existingFilter, namedSearchFilterGroup);
        } else {

            boolean foundFilter = false;

            for (NamedSearchFilterGroup filter : user.getSavedFilters()) {
                if (filter.getName().equals(namedSearchFilterGroup.getName())) {
                    filter.getNamedSearchFilters().clear();
                    filter = namedSearchFilterGroupRepo.clone(filter, namedSearchFilterGroup);
                    foundFilter = true;
                    break;
                }
            }

            if (!foundFilter) {
                namedSearchFilterGroup.setUser(user);
                user.getSavedFilters().add(namedSearchFilterGroupRepo.createFromFilter(namedSearchFilterGroup));
            }

        }

        userRepo.save(user);

        return new ApiResponse(SUCCESS);
    }

}
