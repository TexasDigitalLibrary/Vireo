package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission-list")
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
    private NamedSearchFilterRepo filterCriterionRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @ApiMapping("/all-columns")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getSubmissionViewColumns() {
        return new ApiResponse(SUCCESS, submissionListColumnRepo.findAll());
    }

    @Transactional
    @ApiMapping("/columns-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getSubmissionViewColumnsByUser(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    @Transactional
    @ApiMapping("/filter-columns-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getFilterColumnsByUser(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @Transactional
    @ApiMapping("/pagesize-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getSubmissionViewPageSizeByUser(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, user.getPageSize());
    }

    @Transactional
    @Auth(role = "STUDENT")
    @ApiMapping(value = "/update-user-columns/{pageSize}", method = POST)
    public ApiResponse updateUserSubmissionViewColumns(@ApiCredentials Credentials credentials, @ApiVariable Integer pageSize, @ApiModel List<SubmissionListColumn> submissionViewColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.setPageSize(pageSize);
        user.setSubmissionViewColumns(submissionViewColumns);
        user = userRepo.save(user);

        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    @Transactional
    @ApiMapping("/reset-user-columns")
    @Auth(role = "STUDENT")
    public ApiResponse resetUserSubmissionViewColumns(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.setSubmissionViewColumns(defaultSubmissionListColumnService.getDefaultSubmissionListColumns());
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }

    @Transactional
    @Auth(role = "STUDENT")
    @ApiMapping(value = "/update-user-filter-columns", method = POST)
    public ApiResponse updateUserFilterColumns(@ApiCredentials Credentials credentials, @ApiModel List<SubmissionListColumn> filterColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.setFilterColumns(filterColumns);
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/set-active-filter", method = POST)
    public ApiResponse setActiveFilter(@ApiCredentials Credentials credentials, @ApiValidatedModel NamedSearchFilterGroup namedSearchFilterGroup) {

        User user = userRepo.findByEmail(credentials.getEmail());

        List<Long> ids = new ArrayList<Long>();

        user.getActiveFilter().getNamedSearchFilters().forEach(namedSearchFilter -> {
            ids.add(namedSearchFilter.getId());
        });

        user.getActiveFilter().getNamedSearchFilters().clear();

        user = userRepo.save(user);

        ids.forEach(id -> {
            filterCriterionRepo.delete(id);
        });

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        activeFilter = namedSearchFilterGroupRepo.clone(activeFilter, namedSearchFilterGroup);

        if (activeFilter.getColumnsFlag()) {
            user.getSubmissionViewColumns().clear();
            user.getSubmissionViewColumns().addAll(user.getActiveFilter().getSavedColumns());
        }

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);

    }

    @ApiMapping("/active-filters")
    @Auth(role = "MANAGER")
    public ApiResponse getActiveFilters(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @ApiMapping("/saved-filters")
    @Auth(role = "MANAGER")
    public ApiResponse getSavedFilters(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove-saved-filter", method = POST)
    public ApiResponse removeSavedFilter(@ApiCredentials Credentials credentials, @ApiModel NamedSearchFilter savedFilter) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.getSavedFilters().remove(savedFilter);
        user = userRepo.save(user);
        namedSearchFilterGroupRepo.delete(savedFilter.getId());
        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/add-filter-criterion", method = POST)
    public ApiResponse addFilterCriterion(@ApiCredentials Credentials credentials, @ApiData JsonNode data) {

        String criterionName = data.get("criterionName").asText();
        String filterValue = data.get("filterValue").asText();
        Boolean exactMatch = data.get("exactMatch").asBoolean();

        JsonNode filterGlossNode = data.get("filterGloss");

        String filterGloss = null;

        if (filterGlossNode != null) {
            filterGloss = filterGlossNode.asText();
        }

        User user = userRepo.findByEmail(credentials.getEmail());

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        NamedSearchFilter namedSearchFilter = null;

        for (NamedSearchFilter criterion : activeFilter.getNamedSearchFilters()) {
            if (criterion.getName().equals(criterionName)) {
                namedSearchFilter = criterion;
                break;
            }
        }

        if (namedSearchFilter == null) {
            namedSearchFilter = filterCriterionRepo.create(submissionListColumnRepo.findByTitle(criterionName));
        }

        namedSearchFilter.addFilter(filterValue, filterGloss);

        namedSearchFilter.setExactMatch(exactMatch);

        namedSearchFilter.setAllColumnSearch(criterionName.equals(SEARCH_BOX_TITLE) ? true : false);

        user.getActiveFilter().addFilterCriterion(namedSearchFilter);

        user = userRepo.save(user);

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove-filter-criterion/{namedSearchFilterName}", method = POST)
    public ApiResponse removeFilterCriterion(@ApiCredentials Credentials credentials, @ApiVariable String namedSearchFilterName, @ApiModel FilterCriterion filterCriterion) {

        User user = userRepo.findByEmail(credentials.getEmail());

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

    @ApiMapping("/clear-filter-criteria")
    @Auth(role = "MANAGER")
    public ApiResponse clearFilterCriteria(@ApiCredentials Credentials credentials) {

        User user = userRepo.findByEmail(credentials.getEmail());

        user.getActiveFilter().getNamedSearchFilters().clear();

        userRepo.save(user);

        user.getActiveFilter().getNamedSearchFilters().forEach(namedSearchFilter -> {
            filterCriterionRepo.delete(namedSearchFilter);
        });

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/all-saved-filter-criteria")
    @Auth(role = "MANAGER")
    public ApiResponse getAllSaveFilterCriteria(@ApiCredentials Credentials credentials) {

        User user = userRepo.findByEmail(credentials.getEmail());

        List<NamedSearchFilterGroup> userSavedFilters = user.getSavedFilters();
        List<NamedSearchFilterGroup> publicSavedFilters = namedSearchFilterGroupRepo.findByPublicFlagTrue();

        List<NamedSearchFilterGroup> allSavedFilters = new ArrayList<NamedSearchFilterGroup>();
        allSavedFilters.addAll(userSavedFilters);
        allSavedFilters.addAll(publicSavedFilters);

        return new ApiResponse(SUCCESS, userSavedFilters);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/save-filter-criteria", method = POST)
    public ApiResponse saveFilterCriteria(@ApiCredentials Credentials credentials, @ApiValidatedModel NamedSearchFilterGroup namedSearchFilterGroup) {

        User user = userRepo.findByEmail(credentials.getEmail());

        NamedSearchFilterGroup existingFilter = namedSearchFilterGroupRepo.findByNameAndPublicFlagTrue(namedSearchFilterGroup.getName());

        if (existingFilter != null) {
            existingFilter = namedSearchFilterGroupRepo.clone(existingFilter, namedSearchFilterGroup);
            user = userRepo.findByEmail(credentials.getEmail());

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
                user.getSavedFilters().add(namedSearchFilterGroupRepo.createFromFilter(namedSearchFilterGroup));
            }

        }

        userRepo.save(user);

        return new ApiResponse(SUCCESS);
    }

}
