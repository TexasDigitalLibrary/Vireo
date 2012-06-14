package controllers;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.ActiveSearchFilter;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;


import play.data.binding.As;
import play.modules.spring.Spring;
import play.mvc.Http.Cookie;
import play.mvc.With;

/**
 * The controller to handle the reviewer interface for searching, filtering,
 * display, and managing ETDS (aka the list, view and log tabs)
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class Review extends AbstractVireoController {

	public final static String SUBMISSION_FILTER_COOKIE_NAME = "SubmissionFilter";
	public final static String ACTION_LOG_FILTER_COOKIE_NAME = "ActionLogFilter";
	
	/**
	 * List page
	 * 
	 * This page handles the filter search operations for finding submissions. Users are able to use a faceted like browsing experience to tailor a list of submissions.
	 * 
	 * Here's an overview of the paramaters expected by this controller:
	 * 
	 * action: The action to take such add/remove a filter parameter. The valid actions are: filterAdd, filterRemove, filterSave, filterDelete, filterLoad, filterClear.
	 * 
	 * type: When the action is to add/remove a filter parameter the type parameter is used to identifier the particular type to work with.
	 * 
	 * value: When the action is to add/remove a filter the value paramater is the actual value to add or remove from the filter search.
	 * 
	 */
	@Security(RoleType.REVIEWER)
	public static void list() {
		
		// Get current parameters
		Person person = context.getPerson();
		
		
		// Step 1: Update the active filter
		//////////
		
		// Load the acive filter from the cookie
		ActiveSearchFilter activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
		Cookie cookie = request.cookies.get(SUBMISSION_FILTER_COOKIE_NAME);
		if (cookie != null) {
			activeFilter.decode(cookie.value);
		} else {
			// There is no submission filter cookie, create one.
			cookie = new Cookie();
			cookie.name = SUBMISSION_FILTER_COOKIE_NAME;
		}
		
		String action = params.get("action");
		if ("filterAdd".equals(action))
			// The user is going to modify the existing active filter by adding a new paramater.
			doAddFilterParameter(activeFilter);
		
		else if ("filterRemove".equals(action)) {
			// The user is going to modify the existing active filter by removing an existing paramater.
			doRemoveFilterParamater(activeFilter);
			
		} else if ("filterSave".equals(action)) {
			// The user is going to save the current active filter to the database.
			String name = params.get("name");
			NamedSearchFilter newFilter = subRepo.createSearchFilter(person, name);
			activeFilter.copyTo(newFilter);
			newFilter.save();
			
		} else if ("filterDelete".equals(action)) {
			// The user is going to delete an existing filter.
			// TODO: Check privileges
			Long filterId = params.get("id",Long.class);
			NamedSearchFilter oldFilter = subRepo.findSearchFilter(filterId);
			oldFilter.delete();
			
		} else if ("filterLoad".equals(action)) {
			// The user is going to replace the current filter with an existing
			// filter saved in the database.
			// TODO: check privileges
			Long filterId = params.get("id",Long.class);
			NamedSearchFilter savedFilter = subRepo.findSearchFilter(filterId);
			activeFilter.copyFrom(savedFilter);
			
		} else if ("filterClear".equals(action)) {
			// Reset the users current filter by clearing it completely.
			activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
			
		}
		
		// Save the active filter to a cookie
		cookie.value = activeFilter.encode();
		response.cookies.put(SUBMISSION_FILTER_COOKIE_NAME, cookie);
		
		// Step 2:  Run the current filter search
		//////////
		SearchOrder order = SearchOrder.ID;
		Integer orderId = params.get("order",Integer.class);
		if (orderId != null)
			order = SearchOrder.find(orderId);
		
		SearchDirection direction = SearchDirection.ASCENDING;
		Integer directionId = params.get("direction",Integer.class);
		if (directionId != null)
			direction = SearchDirection.find(directionId);
		
		Integer offset = params.get("offset",Integer.class);
		if (offset == null)
			offset = 0;
		
		// TODO: Look up the limit based upon the user's preferences.
		Integer limit = 100;
		
		SearchResult<Submission> results = null;//subRepo.filterSearchSubmissions(activeFilter,order, direction, offset, limit);
		

		// Step 3: Prepare any variables for display
		//////////
		List<NamedSearchFilter> allFilters = subRepo.findSearchFiltersByCreatorOrPublic(person);
		String nav = "list";
		
		render(nav, allFilters, activeFilter, results);
	}
	
	public static void view() {
		String nav = "view";
		render(nav);
	}

	public static void log() {
		String nav = "log";
		render(nav);
	}


	
	
	
	/**
	 * Internal method to handle modification of the active search filter when
	 * adding new parameters to the filter.
	 * 
	 * This method expects that on the params are "type", and "value". Type will
	 * be a textual representation of the search filter parameter type such as
	 * "text", or "state" etc... While value will be the actual value of the new
	 * parameter to be added.
	 * 
	 * There is one special case for date range searching. In this case the
	 * values should be located in the parameter "startDate" and "endDate" which
	 * need to be formated as a date.
	 * 
	 * @param activeFilter
	 *            The active filter to modifiy.
	 */
	protected static void doAddFilterParameter(SearchFilter activeFilter) {
		
		String type = params.get("type");
		String value = params.get("value");
		
		if ("text".equals(type)) {
			activeFilter.addSearchText(value);
			
		} else if ("state".equals(type)) {
			activeFilter.addState(value);
			
		} else if ("assignee".equals(type)) {
			Long personId = params.get("value",Long.class);
			Person person = personRepo.findPerson(personId);
			activeFilter.addAssignee(person);
			
		} else if ("gradYear".equals(type)) {
			Integer year = params.get("value",Integer.class);
			activeFilter.addGraduationYear(year);
			
		} else if ("gradMonth".equals(type)) {
			Integer month = params.get("value",Integer.class);
			activeFilter.addGraduationMonth(month);
			
		} else if ("degree".equals(type)) {
			activeFilter.addDegree(value);
			
		} else if ("department".equals(type)) {
			activeFilter.addDepartment(value);
			
		} else if ("college".equals(type)) {
			activeFilter.addCollege(value);
		
		} else if ("major".equals(type)) {
			activeFilter.addMajor(value);
		
		} else if ("docType".equals(type)) {
			activeFilter.addDocumentType(value);
		
		} else if ("umi".equals(type)) {
			Boolean release = params.get("value",Boolean.class);
			activeFilter.setUMIRelease(release);
		
		} else if ("date".equals(type)) {
			// Not sure if this works the way I think it should.
			Date start = params.get("startDate", Date.class);
			Date end = params.get("endDate", Date.class);
			activeFilter.setDateRange(start, end);
			
		} else {
			error("Unable to add an unknown filter paramater.");
		}
	}
	
	/**
	 * Internal method to handle modification of the active search filter when
	 * removing parameters from the filter.
	 * 
	 * This method expects that on the params are "type", and "value". Type will
	 * be a textual representation of the search filter parameter type such as
	 * "text", or "state" etc... While value will be the actual value of the old
	 * parameter to be removed.
	 * 
	 * @param activeFilter
	 *            The active filter to modifiy.
	 */
	protected static void doRemoveFilterParamater(SearchFilter activeFilter) {
		
		String type = params.get("type");
		String value = params.get("value");
		
		
		if ("text".equals(type)) {
			activeFilter.addSearchText(value);
			
		} else if ("state".equals(type)) {
			activeFilter.addState(value);
			
		} else if ("assignee".equals(type)) {
			Long personId = params.get("value",Long.class);
			Person person = personRepo.findPerson(personId);
			activeFilter.addAssignee(null);
		
		} else if ("gradYear".equals(type)) {
			Integer year = params.get("value",Integer.class);
			activeFilter.addGraduationYear(year);
		
		} else if ("gradMonth".equals(type)) {
			Integer month = params.get("value",Integer.class);
			activeFilter.addGraduationMonth(month);
		
		} else if ("degree".equals(type)) {
			activeFilter.addDegree(value);
		
		} else if ("department".equals(type)) {
			activeFilter.addDepartment(value);
		
		} else if ("college".equals(type)) {
			activeFilter.addCollege(value);
		
		} else if ("major".equals(type)) {
			activeFilter.addMajor(value);
		
		} else if ("docType".equals(type)) {
			activeFilter.addDocumentType(value);
		
		} else if ("umi".equals(type)) {
			activeFilter.setUMIRelease(null);
			
		} else if ("date".equals(type)) {
			activeFilter.setDateRange(null, null);
		
		} else {	
			error("Unable to remove an unknown filter paramater.");
		}
	}
	
	
	
}
