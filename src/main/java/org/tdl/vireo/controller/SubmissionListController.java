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
    
    
    
    @ApiMapping("/active-filters")
    @Auth(role = "MANAGER")
    public ApiResponse getActiveFilters(@ApiCredentials Credentials credentials) {
    	User user = userRepo.findByEmail(credentials.getEmail());
  
    	if(user.getActiveFilter().getFilterCriteria().size() < 1) {
    		
    		NamedSearchFilter activeFilter = user.getActiveFilter();
        	FilterCriterion filterCriterionOne = filterCriterionRepo.create(submissionListColumnRepo.findOne(1L));
        	filterCriterionOne.setName("ID");
        	filterCriterionOne.addFilter("1");
        	filterCriterionOne.addFilter("2");
        	
        	activeFilter.addFilterCriterion(filterCriterionOne);
        	
        	FilterCriterion filterCriterionTwo = filterCriterionRepo.create(submissionListColumnRepo.findOne(2L));
        	filterCriterionTwo.setName("Last Name");
        	filterCriterionTwo.addFilter("Huff");
        	filterCriterionTwo.addFilter("Daniels");
 
        	activeFilter.addFilterCriterion(filterCriterionTwo);
        	
        	userRepo.save(user);
    	}
    	
        return new ApiResponse(SUCCESS,user.getActiveFilter());
    }
    
    @ApiMapping("/saved-filters")
    @Auth(role = "MANAGER")
    public ApiResponse getSavedFilters(@ApiCredentials Credentials credentials) {
    	User user = userRepo.findByEmail(credentials.getEmail());
  
    	if(user.getActiveFilter().getFilterCriteria().size() < 1) {
    		
    		NamedSearchFilter activeFilter = user.getActiveFilter();
        	FilterCriterion filterCriterionOne = filterCriterionRepo.create(submissionListColumnRepo.findOne(1L));
        	filterCriterionOne.setName("ID");
        	filterCriterionOne.addFilter("1");
        	filterCriterionOne.addFilter("2");
        	
        	activeFilter.addFilterCriterion(filterCriterionOne);
        	
        	FilterCriterion filterCriterionTwo = filterCriterionRepo.create(submissionListColumnRepo.findOne(2L));
        	filterCriterionTwo.setName("Last Name");
        	filterCriterionTwo.addFilter("Huff");
        	filterCriterionTwo.addFilter("Daniels");
 
        	activeFilter.addFilterCriterion(filterCriterionTwo);
        	
        	userRepo.save(user);
    	}
    	
        return new ApiResponse(SUCCESS,user.getActiveFilter());
    }

    @ApiMapping("/clear-filter-criterion/{filterCriterionId}")
    @Auth(role = "MANAGER")
    public ApiResponse clearFilterCriterion(@ApiCredentials Credentials credentials, @ApiVariable Long filterCriterionId, @ApiData JsonNode data) {
    	
    	String filterString = data.get("filterString").asText();
    	User user = userRepo.findByEmail(credentials.getEmail());
    	NamedSearchFilter activeFilter = user.getActiveFilter();
    	FilterCriterion filterCriterion = activeFilter.getFilterCriterion(filterCriterionId);
    	
    	filterCriterion.removeFilter(filterString);
    	
    	if (filterCriterion.getFilters().size() == 0) {
        	user.getActiveFilter().removeFilterCriterion(filterCriterion);
    	}
    	
    	userRepo.save(user);
        
    	simpMessagingTemplate.convertAndSend("/channel/active-filters/"+user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/clear-filter-criteria")
    @Auth(role = "MANAGER")
    public ApiResponse clearFilterCriteria(@ApiCredentials Credentials credentials) {
    	
    	User user = userRepo.findByEmail(credentials.getEmail());
    	
    	user.getActiveFilter().getFilterCriteria().clear();
    	
    	userRepo.save(user);
            	
    	simpMessagingTemplate.convertAndSend("/channel/active-filters/"+user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));

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
    	    	
    	user.getSavedFilters().add(namedSearchFilter);
    	
    	userRepo.save(user);
            	
        return new ApiResponse(SUCCESS);
    }
    
}
