package controllers;

import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.EmbargoType;
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
import org.tdl.vireo.state.State;


import play.Logger;
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
			try {
				activeFilter.decode(cookie.value);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to decode search filter: "+cookie.value);
			}
		}
		
		// Step 2:  Run the current filter search
		//////////
		// TODO: we'll probably need to grab these from the session?
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
	
	@Security(RoleType.REVIEWER)
	public static void modifyFilter(String nav) {
		
		Person person = context.getPerson();
		
		// Load the active filter from the cookie
		ActiveSearchFilter activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
		Cookie cookie = request.cookies.get(SUBMISSION_FILTER_COOKIE_NAME);
		if (cookie != null) {
			try {
				activeFilter.decode(cookie.value);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to decode search filter: "+cookie.value);
			}
		}
		
		String action = params.get("action");
		if ("add".equals(action)) {
			System.out.println("add");
			// The user is going to modify the existing active filter by adding a new paramater.
			doAddFilterParameter(activeFilter);
		
		} else if ("remove".equals(action)) {
			System.out.println("remove");

			// The user is going to modify the existing active filter by removing an existing paramater.
			doRemoveFilterParamater(activeFilter);
			
		} else if ("save".equals(action)) {
			System.out.println("save");

			// The user is going to save the current active filter to the database.
			String name = params.get("name");
			NamedSearchFilter newFilter = subRepo.createSearchFilter(person, name);
			activeFilter.copyTo(newFilter);
			newFilter.save();
			
		} else if ("delete".equals(action)) {
			System.out.println("delete");

			// The user is going to delete an existing filter.
			// TODO: Check privileges
			Long filterId = params.get("id",Long.class);
			NamedSearchFilter oldFilter = subRepo.findSearchFilter(filterId);
			oldFilter.delete();
			
		} else if ("load".equals(action)) {
			System.out.println("load");

			// The user is going to replace the current filter with an existing
			// filter saved in the database.
			// TODO: check privileges
			Long filterId = params.get("id",Long.class);
			NamedSearchFilter savedFilter = subRepo.findSearchFilter(filterId);
			activeFilter.copyFrom(savedFilter);
			
		} else if ("clear".equals(action)) {
			System.out.println("clear");

			// Reset the users current filter by clearing it completely.
			activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
			
		} else {
			error("Unknown filter modification action");
		}
		
		// Save the active filter to a cookie
		System.out.println("cookie value="+activeFilter.encode());
		response.setCookie(SUBMISSION_FILTER_COOKIE_NAME, activeFilter.encode());
		
		if ("list".equals(nav))
			list();
		if ("log".equals(nav))
			log();
		
		error("Unknown list modify navigation controll");
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
	 * There are a few special case for date range searching: startDate, endDate,
	 *  year, and month.
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
			if ("null".equals(params.get("value"))) {
				// Unassigned
				activeFilter.addAssignee(null);
			} else {
				// A specific person
				Long personId = params.get("value",Long.class);
				Person person = personRepo.findPerson(personId);
				activeFilter.addAssignee(person);
			}
			
		} else if ("embargo".equals(type)) {
			Long embargoId = params.get("value",Long.class);
			EmbargoType embargo = settingRepo.findEmbargoType(embargoId);
			activeFilter.addEmbargoType(embargo);
			
		} else if ("semester".equals(type)) {
			Integer year = params.get("year",Integer.class);
			Integer month = params.get("month",Integer.class);
			activeFilter.addGraduationSemester(year, month);

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
		
		} else if ("range".equals(type)) {
			// Not sure if this works the way I think it should.
			Date startDate = params.get("startDate", Date.class);
			Date endDate = params.get("endDate", Date.class);
			
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			
			start.setTime(startDate);
			end.setTime(endDate);
			
			// Always set the maximal hour, minute, second so that the dates are inclusive.
			start.set(Calendar.HOUR,end.getActualMinimum(Calendar.HOUR));
			start.set(Calendar.MINUTE,end.getActualMinimum(Calendar.MINUTE));
			start.set(Calendar.SECOND,end.getActualMinimum(Calendar.SECOND));
			end.set(Calendar.HOUR,end.getActualMaximum(Calendar.HOUR));
			end.set(Calendar.MINUTE,end.getActualMaximum(Calendar.MINUTE));
			end.set(Calendar.SECOND,end.getActualMaximum(Calendar.SECOND));
			
			if (start != null)
				activeFilter.setSubmissionDateRangeStart(start.getTime());

			if (end != null)
				activeFilter.setSubmissionDateRangeEnd(end.getTime());
		} else if ("rangeChoose".equals(type)) {
			
			Integer year = params.get("year",Integer.class);
			Integer month = params.get("month",Integer.class);
			Integer days = params.get("days",Integer.class);
			
			// Generate the start & end dates
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();

			start.clear();
			end.clear();
			
			start.set(Calendar.YEAR, year);
			end.set(Calendar.YEAR, year);
			
			// Check if user has chosen down to the month.
			if (month == null) {
				start.set(Calendar.MONTH, Calendar.JANUARY);
				end.set(Calendar.MONTH, Calendar.DECEMBER);
			} else {
				start.set(Calendar.MONTH, month);
				end.set(Calendar.MONTH, month);
			}
			
			// Check if the user has chosen down to the day range.
			if (days == null) {
				start.set(Calendar.DAY_OF_MONTH,end.getActualMinimum(Calendar.DAY_OF_MONTH));
				end.set(Calendar.DAY_OF_MONTH,end.getActualMaximum(Calendar.DAY_OF_MONTH));
			} else {
				// Three cases:
				// 1 = days 1-10 of the current month
				// 11 = days 11-20 of the current month
				// 21 = days 21-31 (or whatever is the last day) of the current month.
				if (days == 1) {
					start.set(Calendar.DAY_OF_MONTH,1);
					end.set(Calendar.DAY_OF_MONTH,10);
				} else if (days == 11) {
					start.set(Calendar.DAY_OF_MONTH,11);
					end.set(Calendar.DAY_OF_MONTH,20);
				} else if (days == 21) {
					start.set(Calendar.DAY_OF_MONTH,21);
					end.set(Calendar.DAY_OF_MONTH,end.getActualMaximum(Calendar.DAY_OF_MONTH));
				}
				
			}
			
			// Always set the maximal hour, minute, second so that the dates are inclusive.
			start.set(Calendar.HOUR,end.getActualMinimum(Calendar.HOUR));
			start.set(Calendar.MINUTE,end.getActualMinimum(Calendar.MINUTE));
			start.set(Calendar.SECOND,end.getActualMinimum(Calendar.SECOND));
			end.set(Calendar.HOUR,end.getActualMaximum(Calendar.HOUR));
			end.set(Calendar.MINUTE,end.getActualMaximum(Calendar.MINUTE));
			end.set(Calendar.SECOND,end.getActualMaximum(Calendar.SECOND));
			
			
			// Set the range
			activeFilter.setSubmissionDateRangeStart(start.getTime());
			activeFilter.setSubmissionDateRangeEnd(end.getTime());			
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
	 * There are a few special case for date range searching: startDate, endDate,
	 * year, and month..
	 * 
	 * @param activeFilter
	 *            The active filter to modifiy.
	 */
	protected static void doRemoveFilterParamater(SearchFilter activeFilter) {
		
		String type = params.get("type");
		String value = params.get("value");
		
		
		if ("text".equals(type)) {
			activeFilter.removeSearchText(value);
			
		} else if ("state".equals(type)) {
			activeFilter.removeState(value);
			
		} else if ("assignee".equals(type)) {
			if ("null".equals(params.get("value"))) {
				// Unassigned
				activeFilter.removeAssignee(null);
			} else {
				// A specific person
				Long personId = params.get("value",Long.class);
				Person person = personRepo.findPerson(personId);
				activeFilter.removeAssignee(person);
			}
		} else if ("embargo".equals(type)) {
			Long embargoId = params.get("value",Long.class);
			EmbargoType embargo = settingRepo.findEmbargoType(embargoId);
			activeFilter.removeEmbargoType(embargo);
			
		} else if ("semester".equals(type)) {
			Integer year = params.get("year",Integer.class);
			Integer month = params.get("month",Integer.class);
			activeFilter.removeGraduationSemester(year, month);

		} else if ("degree".equals(type)) {
			activeFilter.removeDegree(value);
		
		} else if ("department".equals(type)) {
			activeFilter.removeDepartment(value);
		
		} else if ("college".equals(type)) {
			activeFilter.removeCollege(value);
		
		} else if ("major".equals(type)) {
			activeFilter.removeMajor(value);
		
		} else if ("docType".equals(type)) {
			activeFilter.removeDocumentType(value);

		} else if ("umi".equals(type)) {
			activeFilter.setUMIRelease(null);
			
		} else if ("rangeStart".equals(type)) {
			activeFilter.setSubmissionDateRangeStart(null);
		
		} else if ("rangeEnd".equals(type)) {
			activeFilter.setSubmissionDateRangeEnd(null);

		} else {	
			error("Unable to remove an unknown filter paramater.");
		}
	}
	
	
	
}
