package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
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
    @ApiMapping("/update-user-columns/{pageSize}")
    @Auth(role = "STUDENT")
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
    @ApiMapping("/update-user-filter-columns")
    @Auth(role = "STUDENT")
    public ApiResponse updateUserFilterColumns(@ApiCredentials Credentials credentials, @ApiModel List<SubmissionListColumn> filterColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.setFilterColumns(filterColumns);
        user = userRepo.save(user);

        return new ApiResponse(SUCCESS, user.getFilterColumns());
    }

    @ApiMapping("/set-active-filter")
    @Auth(role = "MANAGER")
    public ApiResponse setActiveFilter(@ApiCredentials Credentials credentials, @ApiValidatedModel NamedSearchFilter filter) {

        User user = userRepo.findByEmail(credentials.getEmail());

        List<Long> ids = new ArrayList<Long>();

        user.getActiveFilter().getFilterCriteria().forEach(filterCriterion -> {
            ids.add(filterCriterion.getId());
        });

        user.getActiveFilter().getFilterCriteria().clear();

        user = userRepo.save(user);

        ids.forEach(id -> {
            filterCriterionRepo.delete(id);
        });

        NamedSearchFilter activeFilter = user.getActiveFilter();
        activeFilter = namedSearchFilterRepo.clone(activeFilter, filter);

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

        if (user.getActiveFilter().getFilterCriteria().size() < 1) {

            NamedSearchFilter activeFilter = user.getActiveFilter();
            FilterCriterion filterCriterionOne = filterCriterionRepo.create(submissionListColumnRepo.findByTitle("First Name"));
            filterCriterionOne.setName("First Name");
            filterCriterionOne.addFilter("Jeremy");
            filterCriterionOne.addFilter("Jack");

            activeFilter.addFilterCriterion(filterCriterionOne);

            FilterCriterion filterCriterionTwo = filterCriterionRepo.create(submissionListColumnRepo.findByTitle("Last Name"));
            filterCriterionTwo.setName("Last Name");
            filterCriterionTwo.addFilter("Huff");
            filterCriterionTwo.addFilter("Daniels");

            activeFilter.addFilterCriterion(filterCriterionTwo);

            userRepo.save(user);
        }

        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @ApiMapping("/saved-filters")
    @Auth(role = "MANAGER")
    public ApiResponse getSavedFilters(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());

        if (user.getActiveFilter().getFilterCriteria().size() < 1) {

            NamedSearchFilter activeFilter = user.getActiveFilter();
            FilterCriterion filterCriterionOne = filterCriterionRepo.create(submissionListColumnRepo.findByTitle("First Name"));
            filterCriterionOne.setName("First Name");
            filterCriterionOne.addFilter("Jeremy");
            filterCriterionOne.addFilter("Jack");

            activeFilter.addFilterCriterion(filterCriterionOne);

            FilterCriterion filterCriterionTwo = filterCriterionRepo.create(submissionListColumnRepo.findByTitle("Last Name"));
            filterCriterionTwo.setName("Last Name");
            filterCriterionTwo.addFilter("Huff");
            filterCriterionTwo.addFilter("Daniels");

            activeFilter.addFilterCriterion(filterCriterionTwo);

            userRepo.save(user);
        }

        return new ApiResponse(SUCCESS, user.getActiveFilter());
    }

    @ApiMapping("/remove-saved-filter")
    @Auth(role = "MANAGER")
    public ApiResponse removeSavedFilter(@ApiCredentials Credentials credentials, @ApiModel NamedSearchFilter savedFilter) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.getSavedFilters().remove(savedFilter);
        userRepo.save(user);
        namedSearchFilterRepo.delete(savedFilter.getId());

        return getSavedFilters(credentials);
    }

    @ApiMapping("/remove-filter-criterion")
    @Auth(role = "MANAGER")
    public ApiResponse clearFilterCriterion(@ApiCredentials Credentials credentials, @ApiData JsonNode data) {

        String criterionName = data.get("criterionName").asText();
        String filterValue = data.get("filterValue").asText();

        User user = userRepo.findByEmail(credentials.getEmail());

        NamedSearchFilter activeFilter = user.getActiveFilter();

        for (FilterCriterion criterion : activeFilter.getFilterCriteria()) {
            if (criterion.getName().equals(criterionName)) {
                for (String filter : criterion.getFilters()) {
                    if (filter.equals(filterValue)) {
                        criterion.removeFilter(filterValue);
                        if (criterion.getFilters().size() == 0) {
                            activeFilter.removeFilterCriterion(criterion);
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

        user.getActiveFilter().getFilterCriteria().clear();

        userRepo.save(user);

        user.getActiveFilter().getFilterCriteria().forEach(filterCriterion -> {
            filterCriterionRepo.delete(filterCriterion);
        });

        simpMessagingTemplate.convertAndSend("/channel/active-filters/" + user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/all-saved-filter-criteria")
    @Auth(role = "MANAGER")
    public ApiResponse getAllSaveFilterCriteria(@ApiCredentials Credentials credentials) {

        User user = userRepo.findByEmail(credentials.getEmail());

        List<NamedSearchFilter> userSavedFilters = user.getSavedFilters();
        List<NamedSearchFilter> publicSavedFilters = namedSearchFilterRepo.findByPublicFlagTrue();

        List<NamedSearchFilter> allSavedFilters = new ArrayList<NamedSearchFilter>();
        allSavedFilters.addAll(userSavedFilters);
        allSavedFilters.addAll(publicSavedFilters);

        return new ApiResponse(SUCCESS, userSavedFilters);
    }

    @ApiMapping("/save-filter-criteria")
    @Auth(role = "MANAGER")
    public ApiResponse saveFilterCriteria(@ApiCredentials Credentials credentials, @ApiValidatedModel NamedSearchFilter namedSearchFilter) {

        User user = userRepo.findByEmail(credentials.getEmail());

        NamedSearchFilter existingFilter = namedSearchFilterRepo.findByNameAndPublicFlagTrue(namedSearchFilter.getName());

        if (existingFilter != null) {
            existingFilter = namedSearchFilterRepo.clone(existingFilter, namedSearchFilter);
            user = userRepo.findByEmail(credentials.getEmail());

        } else {

            boolean foundFilter = false;

            for (NamedSearchFilter filter : user.getSavedFilters()) {
                if (filter.getName().equals(namedSearchFilter.getName())) {
                    filter.getFilterCriteria().clear();
                    filter = namedSearchFilterRepo.clone(filter, namedSearchFilter);
                    foundFilter = true;
                    break;

                }
            }

            if (!foundFilter) {
                user.getSavedFilters().add(namedSearchFilterRepo.createFromFilter(namedSearchFilter));
            }

        }

        userRepo.save(user);

        return new ApiResponse(SUCCESS);
    }

}
