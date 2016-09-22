package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.FilterString;
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
    
    @PersistenceContext
    EntityManager em;
    
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
    
    @ApiMapping("/set-active-filter")
    @Auth(role = "MANAGER")
    public ApiResponse setActiveFilter(@ApiCredentials Credentials credentials, @ApiValidatedModel NamedSearchFilter filter) {
    	    	
    	User user = userRepo.findByEmail(credentials.getEmail());
    	
    	filter.setName(null);
    	NamedSearchFilter filterCopy = cloneFilter(filter);
    	
    	user.loadActiveFilter(filterCopy);
    	
    	namedSearchFilterRepo.delete(filterCopy);
    	
    	userRepo.save(user);
    	
    	simpMessagingTemplate.convertAndSend("/channel/active-filters/"+user.getActiveFilter().getId(), new ApiResponse(SUCCESS, user.getActiveFilter()));
    	    	
    	return new ApiResponse(SUCCESS);
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

    @ApiMapping("/remove-filter-criterion")
    @Auth(role = "MANAGER")
    public ApiResponse clearFilterCriterion(@ApiCredentials Credentials credentials, @ApiData JsonNode data) {
    	
    	String criterionName = data.get("criterionName").asText();
    	String filterValue = data.get("filterValue").asText();
    	
    	User user = userRepo.findByEmail(credentials.getEmail());
    	
    	NamedSearchFilter activeFilter = user.getActiveFilter();
    	
    	for(FilterCriterion criterion : activeFilter.getFilterCriteria()) {
    		if(criterion.getName().equals(criterionName)) {
	    		for(String filter : criterion.getFilters()) {
	    			if(filter.equals(filterValue)) {
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
    	user.getSavedFilters().add(cloneFilter(namedSearchFilter));
    	
    	userRepo.save(user);
            	
        return new ApiResponse(SUCCESS);
    }

    private NamedSearchFilter cloneFilter(NamedSearchFilter namedSearchFilter) {
    	NamedSearchFilter newNamedSearchFilter = namedSearchFilterRepo.create(namedSearchFilter.getUser());
    	newNamedSearchFilter.setName(namedSearchFilter.getName());
    	newNamedSearchFilter.setPublicFlag(namedSearchFilter.getPublicFlag());
    	newNamedSearchFilter.setUmiRelease(namedSearchFilter.getUmiRelease());
    	newNamedSearchFilter.setColumnsFlag(namedSearchFilter.getColumnsFlag());
    	namedSearchFilter.getFilterCriteria().forEach(filterCriterion -> {
    		newNamedSearchFilter.addFilterCriterion(cloneFilterCriterion(filterCriterion));
    	});
    	
    	namedSearchFilter.getSavedColumns().forEach(column -> {
    		newNamedSearchFilter.addSavedColumn(column);
    	});

    	return namedSearchFilterRepo.save(newNamedSearchFilter);
    }

	private FilterCriterion cloneFilterCriterion(FilterCriterion filterCriterion) {
		FilterCriterion newFilterCriterion = filterCriterionRepo.create(filterCriterion.getSubmissionListColumn());
		
		newFilterCriterion.setName(filterCriterion.getName());
		filterCriterion.getFilters().forEach(filter -> {
			newFilterCriterion.addFilter(filter);
		});
		
		return filterCriterionRepo.save(newFilterCriterion);
	}
}
