package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.batch.AssignService;
import org.tdl.vireo.batch.CommentService;
import org.tdl.vireo.batch.DeleteService;
import org.tdl.vireo.batch.TransitionService;
import org.tdl.vireo.export.ChunkStream;
import org.tdl.vireo.export.ExportService;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaNamedSearchFilterImpl;
import org.tdl.vireo.search.ActiveSearchFilter;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFacet;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.state.State;

import play.Logger;
import play.libs.F.Promise;
import play.modules.spring.Spring;
import play.mvc.Catch;
import play.mvc.Http.Cookie;
import play.mvc.With;

/**
 * The controller to handle the reviewer interface for searching, filtering,
 * display, and managing ETDS (aka the list, view and log tabs)
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author James Creel (http://www.jamescreel.net)
 */
@With(Authentication.class)
public class FilterTab extends AbstractVireoController {

	public static final String COOKIE_DURATION = "9999d";
	
	// Service to handle batch deposits
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static TransitionService transitionService = Spring.getBeanOfType(TransitionService.class);
	public static DeleteService deleteService = Spring.getBeanOfType(DeleteService.class);
	public static AssignService assignService = Spring.getBeanOfType(AssignService.class);
	public static CommentService commentService = Spring.getBeanOfType(CommentService.class);

	
	// Store the cookie and session names in an easy to lookup two dimensional
	// array, so that modifySearch() and modifyFilter() can be easily coded to
	// support both sets of names. This allows you to do:
	// NAMES[SUBMISSION][ACTIVE_FILTER] to get the name of the cookie for the
	// submission list's active filter. Or easily switch that to lookup the same
	// name for the action log.
	public final static String[][] NAMES = {
		{
			"SubmissionFilter",
			"SubmissionDirection",
			"SubmissionOrderBy",
			"SubmissionOffset",
			"SubmissionFacets",
			"SubmissionResultsPerPage",
		},
		{
			"ActionLogFilter",
			"ActionLogDirection",
			"ActionLogOrderBy",
			"ActionLogOffset",
			"ActionLogFacets",
			"ActionLogResultsPerPage"
		}
	};
	
	// Static index lookups into the NAMES array for name sets.
	public final static int SUBMISSION = 0;
	public final static int ACTION_LOG = 1;
	
	// Static index lookups into the NAMES array for particular names.
	public final static int ACTIVE_FILTER = 0;
	public final static int DIRECTION = 1;
	public final static int ORDERBY = 2;
	public final static int OFFSET = 3;
	public final static int FACETS = 4;
	public final static int RESULTSPERPAGE = 5;
	
	/**
	 * Redirect to the list page.
	 */
	@Security(RoleType.REVIEWER)
	public static void listRedirect() {
		list();
	}
	
	
	/**
	 * List page
	 * 
	 * This controller will run the currently active filter and then display the
	 * results. This method does not change any state, instead the
	 * modifyFilter() and modifySearch() methods will handle those modifications
	 * and then redirect back to this method to display the results.
	 * 
	 */
	@Security(RoleType.REVIEWER)
	public static void list() {
		// Get current parameters
		Person person = context.getPerson();
		
		// Step 1: Update the active filter
		//////////
		
		// Load the acive filter from the cookie
		ActiveSearchFilter activeFilter = getActiveSearchFilter(SUBMISSION);
		
		// Step 2:  Run the current filter search
		//////////
		SearchOrder orderby = null;
		try {
			Cookie orderByCookie = request.cookies.get(NAMES[SUBMISSION][ORDERBY]);
			orderby = SearchOrder.find(Integer.valueOf(orderByCookie.value));
		} catch(RuntimeException re) { /* ignore */	}
		if (orderby == null)
			orderby = SearchOrder.ID;
		
		SearchDirection direction = null;
		try {
			Cookie directionCookie = request.cookies.get(NAMES[SUBMISSION][DIRECTION]);
			direction = SearchDirection.find(Integer.valueOf(directionCookie.value));
		} catch(RuntimeException re) { /* ignore */	}
		if (direction == null)
			direction = SearchDirection.ASCENDING;
		
		Integer offset = 0;
		if (session.get(NAMES[SUBMISSION][OFFSET]) != null)
			offset = Integer.valueOf(session.get(NAMES[SUBMISSION][OFFSET]));
		
		// Lookup the results per page
		Integer resultsPerPage = 100;
		Cookie resultsPerPageCookie = request.cookies.get(NAMES[SUBMISSION][RESULTSPERPAGE]);
		if (resultsPerPageCookie != null && resultsPerPageCookie.value != null && resultsPerPageCookie.value.trim().length() > 0) {	
			try {
				resultsPerPage = Integer.valueOf(resultsPerPageCookie.value);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to understand results per page: "+resultsPerPageCookie.value);
			}
		}
				
		SearchResult<Submission> results = searcher.submissionSearch(activeFilter, orderby, direction, offset, resultsPerPage);
		
		// Step 3: Prepare any variables for display
		//////////
		List<NamedSearchFilter> allFilters = subRepo.findSearchFiltersByCreatorOrPublic(person);
		String nav = "list";
		
		// Get a list of columns to display
		List<SearchOrder> columns = activeFilter.getColumns();
		if (columns.size() == 0)
			columns = getDefaultColumns(SUBMISSION);
		
		// Get a list of facets to display
		List<SearchFacet> facets = new ArrayList<SearchFacet>();
		Cookie facetsCookie = request.cookies.get(NAMES[SUBMISSION][FACETS]);
		if (facetsCookie != null && facetsCookie.value != null) {
			try {
				if (facetsCookie.value.length() > 0) {
					String[] ids = facetsCookie.value.split(",");
					for(String id : ids)
						facets.add(SearchFacet.find(Integer.valueOf(id)));
				}
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to decode facet order: "+facetsCookie.value);
			}
		} else {
			facets = getDefaultFacets(SUBMISSION);
		}
				
		// Add all search orders to the view
		for (SearchOrder order : SearchOrder.values())
			renderArgs.put(order.name(), order);
				
		// Add all search facets to the view
		for (SearchFacet facet : SearchFacet.values())
			renderArgs.put("FACET_"+facet.name(), facet);
		
	    // Add ASCENDING and DECENDING to the view
		renderArgs.put(SearchDirection.ASCENDING.name(), SearchDirection.ASCENDING);
		renderArgs.put(SearchDirection.DESCENDING.name(), SearchDirection.DESCENDING);
		
		// All the published states
		renderArgs.put("states", stateManager.getAllStates());
		
		// Get all the deposit locations
		List<DepositLocation> depositLocations = settingRepo.findAllDepositLocations();
		renderArgs.put("depositLocations", depositLocations);
		
		// Get all the packagers (export formats)
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		renderArgs.put("packagers", packagers);

		List<Person> assignees = personRepo.findPersonsByRole(RoleType.REVIEWER);
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();
		
		// get all the custom actions available in the system
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		render(nav, allFilters, activeFilter, results, orderby, columns, facets, direction, resultsPerPage, assignees, templates, actions);
	}
	
	/**
	 * Log page
	 * 
	 * This controller will run the currently active filter and then display the
	 * results. This method does not change any state, instead the
	 * modifyFilter() and modifySearch() methods will handle those modifications
	 * and then redirect back to this method to display the results.
	 * @param subId 
	 */
	@Security(RoleType.REVIEWER)
	public static void log(Long subId) {
		// Get current parameters
		Person person = context.getPerson();
		
		// Step 1: Update the active filter
		//////////
		
		// Load the acive filter from the cookie
		ActiveSearchFilter activeFilter = getActiveSearchFilter(ACTION_LOG);
		
		// Step 2:  Run the current filter search
		//////////
		SearchOrder orderby = null;
		try {
			Cookie orderByCookie = request.cookies.get(NAMES[ACTION_LOG][ORDERBY]);
			orderby = SearchOrder.find(Integer.valueOf(orderByCookie.value));
		} catch(RuntimeException re) { /* ignore */	}
		if (orderby == null)
			orderby = SearchOrder.ID;
		
		SearchDirection direction = null;
		try {
			Cookie directionCookie = request.cookies.get(NAMES[ACTION_LOG][DIRECTION]);
			direction = SearchDirection.find(Integer.valueOf(directionCookie.value));
		} catch(RuntimeException re) { /* ignore */	}
		if (direction == null)
			direction = SearchDirection.ASCENDING;
		
		Integer offset = 0;
		if (session.get(NAMES[ACTION_LOG][OFFSET]) != null)
			offset = Integer.valueOf(session.get(NAMES[ACTION_LOG][OFFSET]));
		
		// Lookup the results per page
		Integer resultsPerPage = 100;
		Cookie resultsPerPageCookie = request.cookies.get(NAMES[ACTION_LOG][RESULTSPERPAGE]);
		if (resultsPerPageCookie != null && resultsPerPageCookie.value != null && resultsPerPageCookie.value.trim().length() > 0) {	
			try {
				resultsPerPage = Integer.valueOf(resultsPerPageCookie.value);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to understand results per page: "+resultsPerPageCookie.value);
			}
		}
		
		//SearchResult<ActionLog> results = subRepo.filterSearchActionLogs(activeFilter,orderby, direction, offset, limit);
		SearchResult<ActionLog> results = searcher.actionLogSearch(activeFilter, orderby, direction, offset, resultsPerPage);

		// Step 3: Prepare any variables for display
		//////////
		List<NamedSearchFilter> allFilters = subRepo.findSearchFiltersByCreatorOrPublic(person);
		String nav = "log";
		
		// Get a list of columns to display
		List<SearchOrder> columns = activeFilter.getColumns();
		if (columns.size() == 0)
			columns = getDefaultColumns(ACTION_LOG);
		
		// Get a list of facets to display
		List<SearchFacet> facets = new ArrayList<SearchFacet>();
		Cookie facetsCookie = request.cookies.get(NAMES[ACTION_LOG][FACETS]);
		if (facetsCookie != null && facetsCookie.value != null) {
			try {
				if (facetsCookie.value.length() > 0) {
					String[] ids = facetsCookie.value.split(",");
					for(String id : ids)
						facets.add(SearchFacet.find(Integer.valueOf(id)));
				}
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to decode facet order: "+facetsCookie.value);
			}
		} else {
			facets = getDefaultFacets(ACTION_LOG);
		}
		
		
		// Add all search orders to the view
		for (SearchOrder order : SearchOrder.values())
			renderArgs.put(order.name(), order);
				
		// Add all search facets to the view
		for (SearchFacet facet : SearchFacet.values())
			renderArgs.put("FACET_"+facet.name(), facet);
		
	    // Add ASCENDING and DECENDING to the view
		renderArgs.put(SearchDirection.ASCENDING.name(), SearchDirection.ASCENDING);
		renderArgs.put(SearchDirection.DESCENDING.name(), SearchDirection.DESCENDING);
		
		// get all the custom actions available in the system
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		render(nav, allFilters, activeFilter, results, orderby, columns, facets, direction, resultsPerPage, actions, subId);
	}
	
	/**
	 * Modify the current search parameters.
	 * 
	 * This includes things like the sort column, direction, and pagination
	 * offset. These parameters are received and updated in the session before
	 * being redirected back to the list() or log() methods.
	 * 
	 * @param nav
	 *            The current mode: list or log.
	 */
	@Security(RoleType.REVIEWER)
	public static void modifySearch(String nav) {
		
		int type = SUBMISSION;
		if ("log".equals(nav))
			type = ACTION_LOG;
				
		String direction = params.get("direction");
		Integer orderby = params.get("orderby",Integer.class);
		Integer offset = params.get("offset", Integer.class);
		
		if (direction != null) {
			// Toggle the current direction.
			Cookie directionCookie = request.cookies.get(NAMES[type][DIRECTION]);
			if (directionCookie == null || String.valueOf(SearchDirection.ASCENDING.getId()).equals(directionCookie.value)) {
				response.setCookie(NAMES[type][DIRECTION], String.valueOf(SearchDirection.DESCENDING.getId()), COOKIE_DURATION);
			} else {
				response.setCookie(NAMES[type][DIRECTION], String.valueOf(SearchDirection.ASCENDING.getId()), COOKIE_DURATION);
			}
			session.remove(NAMES[type][OFFSET]);
		}
		
		if (orderby != null && SearchOrder.find(orderby) != null) {
			response.setCookie(NAMES[type][ORDERBY], String.valueOf(orderby), COOKIE_DURATION);
			session.remove(NAMES[type][OFFSET]);
		}
		
		if (offset != null)
			session.put(NAMES[type][OFFSET], offset);
			
		if ("list".equals(nav))
			list();
		if ("log".equals(nav))
			log(null);
	}
	
	/**
	 * Customize the columns shown, and their order, and the results per page.
	 * 
	 * @param nav list or log
	 */
	@Security(RoleType.REVIEWER)
	public static void customizeSearch(String nav) {

		int type = SUBMISSION;
		if ("log".equals(nav))
			type = ACTION_LOG;

		// The input will be of the form "column_1,column_2,column_3,...". We
		// will split these up and retrieve the actual search object for each
		// one and arrange in a list. This will ensure that they are all valid
		// ids.
		boolean set_default_columns = (params.get("default_columns") != null);
		List<SearchOrder> columns = new ArrayList<SearchOrder>();
		if(!set_default_columns){
			String columnsString = params.get("columns");
			if((columnsString == null || columnsString.length() == 0)) {
				flash.put("error", "You need to at least select 1 visible column!");
				// Send the user off to the appropriate filter tab.
				if ("list".equals(nav))
					list();
				else if ("log".equals(nav))
					log(null);
				else
					error("Unknown customize navigation control type");
			}
			String[] columnIds = columnsString.split(",");
			for (String columnId : columnIds) {
				String[] parts = columnId.split("_");
	
				SearchOrder column = SearchOrder.find(Integer.valueOf(parts[1]));
				columns.add(column);
			}
		} else {
			columns = getDefaultColumns(type);
		}

		// Now that everything has been checked, reform the list into a comma
		// separated list: "1,2,4,5" (notice not the column_part as before)
		String columnsSerialized = "";
		for (SearchOrder column : columns) {
			if (columnsSerialized.length() > 0)
				columnsSerialized += ",";
			columnsSerialized += column.getId();
		}
		ActiveSearchFilter activeFilter = getActiveSearchFilter(type);
		activeFilter.setColumns(columns);
		// Save as a cookie.
		response.setCookie(NAMES[type][ACTIVE_FILTER], activeFilter.encode(), COOKIE_DURATION);
		
		// Handle results per page
		Integer resultsPerPage = params.get("resultsPerPage",Integer.class);
		if (resultsPerPage != null && resultsPerPage > 0 && resultsPerPage <= 1000) {
			response.setCookie(NAMES[type][RESULTSPERPAGE], String.valueOf(resultsPerPage), COOKIE_DURATION);
			session.remove(NAMES[type][OFFSET]);
		}
		
		// Send the user off to the appropriate filter tab.
		if ("list".equals(nav))
			list();
		else if ("log".equals(nav))
			log(null);
		else
			error("Unknown customize navigation control type");
	}
	
	/**
	 * Modify the current active filter.
	 * 
	 * Operations supported are:
	 * 
	 * ADD: Add a new parameter of any type to the currently active filter.
	 * 
	 * REMOVE: Remove a parameter of any type from the currently active filter.
	 * 
	 * SAVE: Save the currently active filter into the database as a named
	 * filter.
	 * 
	 * MANAGE: Permanently remove saved filters from the database.
	 * 
	 * LOAD: Load a saved filter from the database as the currently active
	 * filter.
	 * 
	 * CLEAR: Clear out the currently active filter.
	 * 
	 * @param nav
	 *            The current mode: list or log
	 */
	@Security(RoleType.REVIEWER)
	public static void modifyFilters(String nav) {
		
		Person person = context.getPerson();
		
		int type = SUBMISSION;
		if ("log".equals(nav))
			type = ACTION_LOG;
		
		// Load the active filter from the cookie
		ActiveSearchFilter activeFilter = getActiveSearchFilter(type);
		
		// Load the active SearchOrder from the cookie
		List<SearchOrder> columns = activeFilter.getColumns();
        if (columns.size() == 0)
            columns = getDefaultColumns(type);
		
		String action = params.get("action");
		if ("add".equals(action)) {
			// The user is going to modify the existing active filter by adding a new parameter.
			doAddFilterParameter(activeFilter);
		
		} else if ("remove".equals(action)) {
			// The user is going to modify the existing active filter by removing an existing parameter.
			doRemoveFilterParamater(activeFilter);
			
		} else if ("save".equals(action)) {
			String name = params.get("name");
			boolean publicFlag = false;
			boolean hasColumnsFlag = false;
			if (params.get("public") != null)
				publicFlag = true;
			if (params.get("columns") != null)
				hasColumnsFlag = true;
			
			if (name != null && name.trim().length() > 0 ) {
			
				// Check if a filter already exists for the name.
				JpaNamedSearchFilterImpl namedFilter = (JpaNamedSearchFilterImpl) subRepo.findSearchFilterByCreatorAndName(person, name);
				if (namedFilter == null) {
					namedFilter = (JpaNamedSearchFilterImpl) subRepo.createSearchFilter(person, name);
				}
				
				activeFilter.copyTo(namedFilter);
				namedFilter.setPublic(publicFlag);
				// if we are not saving the columns clear them out (in case they got copied over from activeFilter)
				if(!hasColumnsFlag) {
					namedFilter.setColumns(new ArrayList<SearchOrder>());
				}
				// else if we're saving columns but there weren't any in the copied-over active filter, save the default ones
				else if (hasColumnsFlag && namedFilter.getColumns().size() == 0) {
					namedFilter.setColumns(getDefaultColumns(type));
				}
				namedFilter.save();
			}
			
		} else if ("manage".equals(action)) {
			String[] removeIds = params.getAll("remove");
			if (removeIds != null ) {
				for (String removeId : removeIds) {	
					NamedSearchFilter namedFilter = subRepo.findSearchFilter(Long.valueOf(removeId));
					
					if (namedFilter.getCreator() == person || person.getRole().ordinal() >= RoleType.MANAGER.ordinal())
						namedFilter.delete();
				}
			}
			
		} else if ("load".equals(action)) {
			Long filterId = params.get("filter",Long.class);
			NamedSearchFilter savedFilter = subRepo.findSearchFilter(filterId);
			
			if (savedFilter.isPublic() || savedFilter.getCreator() == person) {
				if(!savedFilter.hasColumns()) {
					List<SearchOrder> activeColumns = activeFilter.getColumns();
					activeFilter.copyFrom(savedFilter);
					activeFilter.setColumns(activeColumns);
				} else {
					activeFilter.copyFrom(savedFilter);
				}
			}
			
		} else if ("clear".equals(action)) {
			// Reset the users current filter by clearing it completely.
			activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
			
		} else {
			error("Unknown filter modification action");
		}
		
		// Save the active filter to a cookie
		response.setCookie(NAMES[type][ACTIVE_FILTER], activeFilter.encode(), COOKIE_DURATION);
		session.remove(NAMES[type][OFFSET]);
		
		if ("list".equals(nav))
			list();
		if ("log".equals(nav))
			log(null);
		
		error("Unknown modify navigation control type");
	}
	
	/**
	 * This is a special method to reset the log filter so that it only is
	 * looking at log items from one particular submission.
	 * 
	 * @param subId
	 *            The submission id.
	 */
	@Security(RoleType.REVIEWER)
	public static void resetLogFilterToOneSubmission(Long subId) {
		
		// Create a new blank filter.
		ActiveSearchFilter activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
		
		// Find the submission and load it.
		Submission sub = subRepo.findSubmission(subId);
		activeFilter.addIncludedSubmission(sub);
		
		// Save the active filter to a cookie
		response.setCookie(NAMES[ACTION_LOG][ACTIVE_FILTER], activeFilter.encode(),COOKIE_DURATION);
		
		// Redirect back to the log page;
		log(subId);
	}
	
	@Security(RoleType.REVIEWER)
	public static void customizeFilters(String nav) {
		
		int type = SUBMISSION;
		if ("log".equals(nav))
			type = ACTION_LOG;

		// The input will be of the form "facet_1,facet_2,facet_3,...". We
		// will split these up and retrieve the actual search object for each
		// one and arrange in a list. This will ensure that they are all valid
		// ids.
		String facetsSerialized = "";

		List<SearchFacet> facets = new ArrayList<SearchFacet>();
		String facetsString = params.get("facets");
		if (facetsString != null && facetsString.trim().length() > 0) {
			String[] facetIds = facetsString.split(",");
			for (String facetId : facetIds) {
				String[] parts = facetId.split("_");
	
				SearchFacet facet = SearchFacet.find(Integer.valueOf(parts[1]));
				facets.add(facet);
			}
	
			// Now that every thing has been checked, reform the list into a comma
			// separated list: "1,2,4,5" (notice not the facet_part as before)
			for (SearchFacet facet : facets) {
				if (facetsSerialized.length() > 0)
					facetsSerialized += ",";
				facetsSerialized += facet.getId();
			}
		}

		// Save as a cookie.
		response.setCookie(NAMES[type][FACETS], facetsSerialized, COOKIE_DURATION);
	
		
		
		// Send the user off to the appropriate filter tab.
		if ("list".equals(nav))
			list();
		if ("log".equals(nav))
			log(null);
		
		error("Unknown customize filter navigation control type");
	}
	
	/**
	 * Deposit and Publish a batch of submissions. After kicking off a
	 * background process the user will be redirected back to the list page.
	 * 
	 * @param depositLocationId
	 *            The id of the location these submissions should be deposited
	 *            into.
	 * @param successState
	 *            The state these should be transitioned into if successful.
	 *            (optional)
	 */
	@Security(RoleType.REVIEWER) 
	public static void batchTransition(String state, Long depositLocationId) {

		// Step 1, get the current filter
		ActiveSearchFilter filter = getActiveSearchFilter(SUBMISSION);
		
		// Step 2, Lookup the location.
		DepositLocation location = null;
		if (depositLocationId != null)
			location = settingRepo.findDepositLocation(depositLocationId);
		
		// Step 3, Resolve the state.
		State stateObject = null;
		if (state != null && state.length() > 0)
			stateObject = stateManager.getState(state);
		
		// Kick off the batch update or delete job
		JobMetadata job = null;
		if (stateObject.isDeletable() && "confirm-delete".equals(params.get("delete-submissions")))
			job = deleteService.delete(filter);
		else 
			job = transitionService.transition(filter, stateObject, location);

		// Show a progress bar
		JobTab.adminStatus(job.getId().toString());
	}
	
	/**
	 * Change the assignee on a batch of submissions. After kicking off a
	 * background process the user will be redirected back to the list page.
	 * 
	 * @param assignTo
	 * 			The id of the person the submissions will be assigned to.
	 */
	@Security(RoleType.REVIEWER)
	public static void batchAssign(Long assignTo) {
		
		// Get the current filter
		ActiveSearchFilter filter = getActiveSearchFilter(SUBMISSION);
		
		// Kick off the batch assign to
		JobMetadata job = assignService.assign(filter, assignTo);
		
		// Show a progress bar
		JobTab.adminStatus(job.getId().toString());
	}
	
	/**
	 * Add a comment/send email on a batch of submissions.
	 */
	@Security(RoleType.REVIEWER)
	public static void batchComment() {
		
		String comment = params.get("comment");
		String subject = params.get("subject");
		Boolean visibility = false;
		
		String primary_recipients_string = null;
		String cc_recipients_string = null;
		
		if("public".equals(params.get("visibility"))){
			visibility = true;
			primary_recipients_string = params.get("primary_recipients");
			cc_recipients_string = params.get("cc_recipients");
		}
		
		
		
		
		// Get the current filter
		ActiveSearchFilter filter = getActiveSearchFilter(SUBMISSION);
		
		// Kick off the batch comment/email
		JobMetadata job = commentService.comment(filter, comment, subject, visibility, primary_recipients_string, cc_recipients_string);
	
		// Show a progress bar
		JobTab.adminStatus(job.getId().toString());
	}
	
	/**
	 * Download a back export. This may take a considerable amount of time, so
	 * we use play's asynchronous features to suspend the current thread until
	 * the next chunk of data is ready to be published.
	 * 
	 * @param packager The packager to use for the export.
	 */
	@Security(RoleType.REVIEWER)
	public static void batchExport() {
		String packager = params.get("packager");
		String packager_extra = params.get("packager-extra");
		long saved_filter_id = -1;
		if(packager_extra != null && packager_extra.equals("Saved")){
			if(params.get("packager-extra-saved-filters") != null) {
				saved_filter_id = Long.parseLong(params.get("packager-extra-saved-filters"));
			} else {
				// Store the error so it can be displayed.
				flash.put("error", "Tried to batch export using a saved filter, but none was selected!");
				list();
			}
		}
			
		// Step 1, get the correct filter (either active or saved)
		ActiveSearchFilter filter = getActiveSearchFilter(SUBMISSION);
		if(saved_filter_id >= 0) {
			filter.copyFrom(subRepo.findSearchFilter(saved_filter_id));
		}
		
		// Step 2, locate the packager
		Packager exportPackage = (Packager) Spring.getBean(packager);
		
		Logger.info("%s (%d: %s) downloaded an export. Export Packager = '%s'\nExport Filter = '%s'",
				context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
				context.getPerson().getId(), 
				context.getPerson().getEmail(),
				exportPackage==null ? "null" : exportPackage.getBeanName(),
				filter==null ? "null" : filter.encode());
		
		// Step 3, Stream the chunks.
		ExportService exportService = (ExportService) Spring.getBean(exportPackage.getExportServiceBeanName());
		ChunkStream stream = exportService.export(exportPackage,filter);
		
		response.contentType = stream.getContentType();
		response.setHeader("Content-Disposition", stream.getContentDisposition());
		
		// Fix problem with no-cache headers and ie8
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control","public");
		
		while(stream.hasNextChunk()) {
			Promise<byte[]> nextChunk = stream.nextChunk();
			byte[] chunk = await(nextChunk);
			
			response.writeChunk(chunk);
		}
	}
	
	/**
	 * When an error occurs it is very likely related to the current
	 * configuration which is saved in cookies, such as the current filter
	 * search, offset, etc... To prevent the user from being trapped in an
	 * endless loop we will record the error in the logs, then clear the current
	 * state and send them back to the list or log pages with a clean slate.
	 * 
	 * The views know to show the error message indicating that the state has 
	 * been cleared.
	 * 
	 * @param throwable
	 */
	@Catch(RuntimeException.class)
	public static void handleError(Throwable throwable) {
		
		Logger.error(throwable, "Error on the List or Log tab, clearing the users session to recover.");
		
		// When an error occurs, clear the current state so the user is not
		// trapped in an endless loop they can't recover from without
		// clearing their cookies. Then save the error message on the flash
		// and report it to the user on their next page view.
		
		// Clear out everything related to submission
		response.removeCookie(NAMES[SUBMISSION][ACTIVE_FILTER]);
		response.removeCookie(NAMES[SUBMISSION][DIRECTION]);
		response.removeCookie(NAMES[SUBMISSION][ORDERBY]);
		response.removeCookie(NAMES[SUBMISSION][FACETS]);
		response.removeCookie(NAMES[SUBMISSION][RESULTSPERPAGE]);
		session.remove(NAMES[SUBMISSION][OFFSET]);

		// Clear out everything related to action logs
		response.removeCookie(NAMES[ACTION_LOG][ACTIVE_FILTER]);
		response.removeCookie(NAMES[ACTION_LOG][DIRECTION]);
		response.removeCookie(NAMES[ACTION_LOG][ORDERBY]);
		response.removeCookie(NAMES[ACTION_LOG][FACETS]);
		response.removeCookie(NAMES[ACTION_LOG][RESULTSPERPAGE]);
		session.remove(NAMES[ACTION_LOG][OFFSET]);
		
		// Store the error so it can be displayed.
		flash.put("error", throwable.getMessage());
		
		// Check for an endless error loop.
		String errorLoop = flash.get("errorLoop");
		flash.put("errorLoop", "maybe");
		
		if (errorLoop == null) {
			// Only redirect if no error loop is detected.
			if ("log".equals(request.actionMethod)) 
				FilterTab.log(null);
			
			if ("list".equals(request.actionMethod)) 
				FilterTab.list();
			
			if ("log".equals(request.routeArgs.get("nav")))
				FilterTab.log(null);
			
			if ("list".equals(request.routeArgs.get("nav")))
				FilterTab.list();
		}
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
		
		if ("include_sub".equals(type)) {
			Long subId = params.get("value",Long.class);
			Submission sub = subRepo.findSubmission(subId);
			activeFilter.addIncludedSubmission(sub);
		} else if ("include_log".equals(type)) {
			Long logId = params.get("value",Long.class);
			ActionLog log = subRepo.findActionLog(logId);
			activeFilter.addIncludedActionLog(log);
		} else if ("exclude_sub".equals(type)) {
			Long subId = params.get("value",Long.class);
			Submission sub = subRepo.findSubmission(subId);
			activeFilter.addExcludedSubmission(sub);
		} else if ("exclude_log".equals(type)) {
			Long logId = params.get("value",Long.class);
			ActionLog log = subRepo.findActionLog(logId);
			activeFilter.addExcludedActionLog(log);
		} else if ("text".equals(type)) {
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
		
		} else if ("program".equals(type)) {
			activeFilter.addProgram(value);
			
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
			
			// First handle start date
			Date startDate = params.get("startDate", Date.class);
			if (startDate != null) {
				Calendar start = Calendar.getInstance();
				
				start.setTime(startDate);
				
				// Always set the minimal hour, minute, second so that the start date is inclusive.
				start.set(Calendar.HOUR,start.getActualMinimum(Calendar.HOUR));
				start.set(Calendar.MINUTE,start.getActualMinimum(Calendar.MINUTE));
				start.set(Calendar.SECOND,start.getActualMinimum(Calendar.SECOND));
				
				activeFilter.setDateRangeStart(start.getTime());
			}
			
			// Next handle end date
			Date endDate = params.get("endDate", Date.class);
			if (endDate != null) {
				Calendar end = Calendar.getInstance();
				
				end.setTime(endDate);
				
				// Always set the maximal hour, minute, second so that the dates are inclusive.
				end.set(Calendar.HOUR,end.getActualMaximum(Calendar.HOUR));
				end.set(Calendar.MINUTE,end.getActualMaximum(Calendar.MINUTE));
				end.set(Calendar.SECOND,end.getActualMaximum(Calendar.SECOND));
				
				activeFilter.setDateRangeEnd(end.getTime());
			}
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
			activeFilter.setDateRangeStart(start.getTime());
			activeFilter.setDateRangeEnd(end.getTime());			
		} else if ("customAction".equals(type)){
			Long customActionId = params.get("value",Long.class);
			CustomActionDefinition customAction = settingRepo.findCustomActionDefinition(customActionId);
			activeFilter.addCustomAction(customAction);
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
		
		if ("include_sub".equals(type)) {
			Long subId = params.get("value",Long.class);
			Submission sub = subRepo.findSubmission(subId);
			activeFilter.removeIncludedSubmission(sub);
			
		} else if ("include_log".equals(type)) {
			Long logId = params.get("value",Long.class);
			ActionLog log = subRepo.findActionLog(logId);
			activeFilter.removeIncludedActionLog(log);
			
		} else if ("exclude_sub".equals(type)) {
			Long subId = params.get("value",Long.class);
			Submission sub = subRepo.findSubmission(subId);
			activeFilter.removeExcludedSubmission(sub);
			
		} else if ("exclude_log".equals(type)) {
			Long logId = params.get("value",Long.class);
			ActionLog log = subRepo.findActionLog(logId);
			activeFilter.removeExcludedActionLog(log);
			
		} else if ("text".equals(type)) {
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
		
		} else if ("program".equals(type)) {
			activeFilter.removeProgram(value);
			
		} else if ("college".equals(type)) {
			activeFilter.removeCollege(value);
		
		} else if ("major".equals(type)) {
			activeFilter.removeMajor(value);
		
		} else if ("docType".equals(type)) {
			activeFilter.removeDocumentType(value);

		} else if ("umi".equals(type)) {
			activeFilter.setUMIRelease(null);
			
		} else if ("rangeStart".equals(type)) {
			activeFilter.setDateRangeStart(null);
		
		} else if ("rangeEnd".equals(type)) {
			activeFilter.setDateRangeEnd(null);

		} else if ("customAction".equals(type)){
			Long customActionId = params.get("value",Long.class);
			CustomActionDefinition customAction = settingRepo.findCustomActionDefinition(customActionId);
			activeFilter.removeCustomAction(customAction);
		} else {	
			error("Unable to remove an unknown filter paramater.");
		}
	}
	
	/**
	 * Return the current active filter
	 * @param type - SUBMISSION or ACTION_LOG
	 * @return - {@link ActiveSearchFilter}
	 */
	private static ActiveSearchFilter getActiveSearchFilter(int type){
		ActiveSearchFilter activeFilter = Spring.getBeanOfType(ActiveSearchFilter.class);
		Cookie cookie = request.cookies.get(NAMES[type][ACTIVE_FILTER]);
		if (cookie != null && cookie.value != null && cookie.value.trim().length() > 0) {
			try {
				activeFilter.decode(cookie.value);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to decode search filter: "+cookie.value);
			}
		}
		return activeFilter;
	}

	/**
	 * Return the default columns when none are set.
	 * 
	 * @param type
	 *            The screen type, either ACTION_LOG or SUBMISSION
	 * @return A list of default columns.
	 */
	public static List<SearchOrder> getDefaultColumns(int type) {
		
		List<SearchOrder> columns = new ArrayList<SearchOrder>();
		if (type == ACTION_LOG) {
			columns.add(SearchOrder.ID);
			columns.add(SearchOrder.STATE);
			columns.add(SearchOrder.ASSIGNEE);
			columns.add(SearchOrder.LAST_EVENT_ENTRY);
			columns.add(SearchOrder.LAST_EVENT_TIME);			
		} else {
			columns.add(SearchOrder.ID);
			columns.add(SearchOrder.STUDENT_NAME);
			columns.add(SearchOrder.STATE);
			columns.add(SearchOrder.ASSIGNEE);
			columns.add(SearchOrder.DOCUMENT_TITLE);
			columns.add(SearchOrder.SUBMISSION_DATE);
			columns.add(SearchOrder.APPROVAL_DATE);
			columns.add(SearchOrder.EMBARGO_TYPE);
		}
		
		return columns;
	}
	
	
	/**
	 * Return the default search facets when none are set.
	 * 
	 * @param type
	 *            The screen type, either ACTION_LOG or SUBMISSION
	 * @return A list of default facets.
	 */
	protected static List<SearchFacet> getDefaultFacets(int type) {
		
		List<SearchFacet> facets = new ArrayList<SearchFacet>();
		if (type == ACTION_LOG) {
			facets.add(SearchFacet.TEXT);
			facets.add(SearchFacet.STATE);
			facets.add(SearchFacet.ASSIGNEE);
			facets.add(SearchFacet.DATE_RANGE);			
		} else {
			facets.add(SearchFacet.TEXT);
			facets.add(SearchFacet.STATE);
			facets.add(SearchFacet.ASSIGNEE);
			facets.add(SearchFacet.GRADUATION_SEMESTER);
		}
		
		return facets;
	}
}
