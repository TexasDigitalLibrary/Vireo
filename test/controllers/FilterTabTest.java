package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.SearchFacet;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.i18n.Messages;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test the FilterTab controller
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class FilterTabTest extends AbstractVireoFunctionalTest {
	
	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static DepositService depositService = Spring.getBeanOfType(DepositService.class);
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	
	/**
	 * Test that we can remove and add each type of parameter (except for the
	 * submission date) from the current action filter.
	 * 
	 * To do this the test will run through each parameter type add it to the
	 * filter, confirm that it has been added. Then once all the parameters have
	 * been added it will verify that they are all still present. Finally in the
	 * same order it will remove them one-by-one.
	 */
	@Test
	public void testAddRemoveEachTypeOfFilterParameter() {
		
		// Login as an administrator
		LOGIN();
		
		
		Person reviewer = personRepo.findPersonsByRole(RoleType.REVIEWER).get(0);
		EmbargoType embargo1 = settingRepo.findAllEmbargoTypes().get(0);
		EmbargoType embargo2 = settingRepo.findAllEmbargoTypes().get(1);
		
	
		// Run for both the list and log tabs
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
			
			
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			
			final String LIST_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
			final String CUSTOMIZE_FILTERS_URL = Router.reverse("FilterTab.customizeFilters",routeArgs).url;

			// Turn on most filters
			String facets = "";
			for (SearchFacet facet: SearchFacet.values()) {
				if (facets.length() > 0)
					facets += ",";
				facets += "facet_"+facet.getId();
			}
			Map<String,String> params = new HashMap<String,String>();
			params.put("facets", facets);
			params.put("submit_save","Save");
			POST(CUSTOMIZE_FILTERS_URL,params);
			
			
			Response response = GET(LIST_URL);
			// Check that there are no filters.
			assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);
			
			// Add Include Submission
			GET(FILTER_URL+"?action=add&type=include_sub&value=1");
			// Add Exclude Submission
			GET(FILTER_URL+"?action=add&type=exclude_sub&value=2");
			// Add STATUS: Submitted
			GET(FILTER_URL+"?action=add&type=state&value=Submitted");
			// Add STATUS: In Progress
			GET(FILTER_URL+"?action=add&type=state&value=InProgress");	
			// Add STATUS: Published
			GET(FILTER_URL+"?action=add&type=state&value=Published");
			// Add ASSGINEE: unassigned
			GET(FILTER_URL+"?action=add&type=assignee&value=null");
			// Add ASSGINEE: Billy
			GET(FILTER_URL+"?action=add&type=assignee&value="+reviewer.getId());
			// Add GRADUATION SEMESTER: 2010 May
			GET(FILTER_URL+"?action=add&type=semester&year=2010&month=4");
			// Add GRADUATION SEMESTER: 2011 August
			GET(FILTER_URL+"?action=add&type=semester&year=2011&month=7");
			// Add DEPARTMENT: Agricultural Leadership, Education and Communications
			GET(FILTER_URL+"?action=add&type=department&value=Agricultural+Leadership%2C+Education%2C+and+Communications");
			// Add DEPARTMENT: Visualization
			GET(FILTER_URL+"?action=add&type=department&value=Visualization");
			// Add COLLEGE: College of Education and Human Development
			GET(FILTER_URL+"?action=add&type=college&value=College+of+Education+and+Human+Development");
			// Add COLLEGE: College of Science
			GET(FILTER_URL+"?action=add&type=college&value=College+of+Science");
			// Add MAJOR: Accounting
			GET(FILTER_URL+"?action=add&type=major&value=Accounting");
			// Add MAJOR: Zoology
			GET(FILTER_URL+"?action=add&type=major&value=Zoology");
			// Add EMBARGO: none
			GET(FILTER_URL+"?action=add&type=embargo&value="+embargo1.getId());
			// Add EMBARGO: Patent Hold
			GET(FILTER_URL+"?action=add&type=embargo&value="+embargo2.getId());
			// Add DEGREE: Doctor of Philosophy
			GET(FILTER_URL+"?action=add&type=degree&value=Doctor+of+Philosophy");
			// Add DEGREE: Bachelor of Environmental Design
			GET(FILTER_URL+"?action=add&type=degree&value=Bachelor+of+Environmental+Design");
			// Add DOCUMENT TYPE: Record of Study
			GET(FILTER_URL+"?action=add&type=docType&value=Record+of+Study");
			// Add DOCUMENT TYPE: Thesis
			GET(FILTER_URL+"?action=add&type=docType&value=Thesis");
			// Add DOCUMENT TYPE: Dissertation
			GET(FILTER_URL+"?action=add&type=docType&value=Dissertation");
			// Add UMI RELEASE: True
			GET(FILTER_URL+"?action=add&type=umi&value=true");

			
			// Now that we are at the apex, check that everything is still there.
			response = GET(LIST_URL);

			assertTrue(getContent(response).contains("filter?action=remove&type=include_sub&value=1"));
			assertTrue(getContent(response).contains("filter?action=remove&type=exclude_sub&value=2"));
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=Submitted"));
			assertFalse(getContent(response).contains("filter?action=add&type=state&value=Submitted"));
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=InProgress"));
			assertFalse(getContent(response).contains("filter?action=add&type=state&value=InProgress"));
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=Published"));
			assertFalse(getContent(response).contains("filter?action=add&type=state&value=Published"));
			assertTrue(getContent(response).contains("filter?action=remove&type=assignee&value=null"));
			assertFalse(getContent(response).contains("filter?action=add&type=assignee&value=null"));
			assertTrue(getContent(response).contains("filter?action=remove&type=assignee&value="+reviewer.getId()));
			assertFalse(getContent(response).contains("filter?action=add&type=assignee&value="+reviewer.getId()));
			assertTrue(getContent(response).contains("filter?action=remove&type=semester&year=2010&month=4"));
			assertFalse(getContent(response).contains("filter?action=add&type=semester&year=2010&month=4"));
			assertTrue(getContent(response).contains("filter?action=remove&type=semester&year=2011&month=7"));
			assertFalse(getContent(response).contains("filter?action=add&type=semester&year=2011&month=7"));
			assertTrue(getContent(response).contains("filter?action=remove&type=department&value=Agricultural+Leadership%2C+Education%2C+and+Communications"));
			assertFalse(getContent(response).contains("filter?action=add&type=department&value=Agricultural+Leadership%2C+Education%2C+and+Communications"));
			assertTrue(getContent(response).contains("filter?action=remove&type=department&value=Visualization"));
			assertFalse(getContent(response).contains("filter?action=add&type=department&value=Visualization"));
			assertTrue(getContent(response).contains("filter?action=remove&type=college&value=College+of+Education+and+Human+Development"));
			assertFalse(getContent(response).contains("filter?action=add&type=college&value=College+of+Education+and+Human+Development"));
			assertTrue(getContent(response).contains("filter?action=remove&type=college&value=College+of+Science"));
			assertFalse(getContent(response).contains("filter?action=add&type=college&value=College+of+Science"));
			assertTrue(getContent(response).contains("filter?action=remove&type=major&value=Accounting"));
			assertFalse(getContent(response).contains("filter?action=add&type=major&value=Accounting"));
			assertTrue(getContent(response).contains("filter?action=remove&type=major&value=Zoology"));
			assertFalse(getContent(response).contains("filter?action=add&type=major&value=Zoology"));		
			assertTrue(getContent(response).contains("filter?action=remove&type=embargo&value="+embargo1.getId()));
			assertFalse(getContent(response).contains("filter?action=add&type=embargo&value="+embargo1.getId()));		
			assertTrue(getContent(response).contains("filter?action=remove&type=embargo&value="+embargo2.getId()));
			assertFalse(getContent(response).contains("filter?action=add&type=embargo&value="+embargo2.getId()));
			assertTrue(getContent(response).contains("filter?action=remove&type=degree&value=Doctor+of+Philosophy"));
			assertFalse(getContent(response).contains("filter?action=add&type=degree&value=Doctor+of+Philosophy"));
			assertTrue(getContent(response).contains("filter?action=remove&type=degree&value=Bachelor+of+Environmental+Design"));
			assertFalse(getContent(response).contains("filter?action=add&type=degree&value=Bachelor+of+Environmental+Design"));
			assertTrue(getContent(response).contains("filter?action=remove&type=docType&value=Record+of+Study"));
			assertFalse(getContent(response).contains("filter?action=add&type=docType&value=Record+of+Study"));
			assertTrue(getContent(response).contains("filter?action=remove&type=docType&value=Thesis"));
			assertFalse(getContent(response).contains("filter?action=add&type=docType&value=Thesis"));
			assertTrue(getContent(response).contains("filter?action=remove&type=docType&value=Dissertation"));
			assertFalse(getContent(response).contains("filter?action=add&type=docType&value=Dissertation"));
			assertTrue(getContent(response).contains("filter?action=remove&type=umi&value=true"));
			assertFalse(getContent(response).contains("filter?action=add&type=umi&value=true"));
			

			// Remove include submission
			GET(FILTER_URL+"?action=remove&type=include_sub&value=1");
			// Remove exclude submission
			GET(FILTER_URL+"?action=remove&type=exclude_sub&value=2");
			// Remove STATUS: Submitted
			GET(FILTER_URL+"?action=remove&type=state&value=Submitted");
			// Remove STATUS: In Progress
			GET(FILTER_URL+"?action=remove&type=state&value=InProgress");	
			// Remove STATUS: Published
			GET(FILTER_URL+"?action=remove&type=state&value=Published");		
			// Remove ASSGINEE: unassigned
			GET(FILTER_URL+"?action=remove&type=assignee&value=null");
			// Remove ASSGINEE: Billy
			GET(FILTER_URL+"?action=remove&type=assignee&value="+reviewer.getId());
			// Remove GRADUATION SEMESTER: 2010 May
			GET(FILTER_URL+"?action=remove&type=semester&year=2010&month=4");
			// Remove GRADUATION SEMESTER: 2011 August
			GET(FILTER_URL+"?action=remove&type=semester&year=2011&month=7");
			// Remove DEPARTMENT: Agricultural Leadership, Education and Communications
			GET(FILTER_URL+"?action=remove&type=department&value=Agricultural+Leadership%2C+Education%2C+and+Communications");
			// Remove DEPARTMENT: Visualization
			GET(FILTER_URL+"?action=remove&type=department&value=Visualization");
			// Remove COLLEGE: College of Education and Human Development
			GET(FILTER_URL+"?action=remove&type=college&value=College+of+Education+and+Human+Development");
			// Remove COLLEGE: College of Science
			GET(FILTER_URL+"?action=remove&type=college&value=College+of+Science");
			// Remove MAJOR: Accounting
			GET(FILTER_URL+"?action=remove&type=major&value=Accounting");
			// Remove MAJOR: Zoology
			GET(FILTER_URL+"?action=remove&type=major&value=Zoology");
			// Remove EMBARGO: none
			GET(FILTER_URL+"?action=remove&type=embargo&value="+embargo1.getId());	
			// Remove EMBARGO: Patent Hold
			GET(FILTER_URL+"?action=remove&type=embargo&value="+embargo2.getId());
			// Remove DEGREE: Doctor of Philosophy
			GET(FILTER_URL+"?action=remove&type=degree&value=Doctor+of+Philosophy");
			// Remove DEGREE: Bachelor of Environmental Design
			GET(FILTER_URL+"?action=remove&type=degree&value=Bachelor+of+Environmental+Design");
			// Remove DOCUMENT TYPE: Record of Study
			GET(FILTER_URL+"?action=remove&type=docType&value=Record+of+Study");
			// Remove DOCUMENT TYPE: Thesis
			GET(FILTER_URL+"?action=remove&type=docType&value=Thesis");
			// Remove DOCUMENT TYPE: Dissertation
			GET(FILTER_URL+"?action=remove&type=docType&value=Dissertation");
			// Remove UMI RELEASE: true
			GET(FILTER_URL+"?action=remove&type=umi&value=true");

			
			// Finally, check that there are no filters left
			response = GET(LIST_URL);
			assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);
		}
		
	}
	
	/**
	 * Test that the submission date works correctly.
	 * 
	 * First we start by using the choose mechanism to narrow down the date to
	 * particular subset of days. Once that is complete we will remove the start
	 * and end dates individually. Finally we will conclude by setting the start
	 * and end dates manually using the range mechanism.
	 */
	@Test
	public void testAddRemoveDateFilter() {
		
		// Login as an administrator
		LOGIN();
		
		// Run for both the list and log tabs
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
			
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			final String LIST_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
			final String CUSTOMIZE_FILTERS_URL = Router.reverse("FilterTab.customizeFilters",routeArgs).url;
			
			// Turn on the date range filters
			String facets = "facet_"+SearchFacet.DATE_CHOOSE.getId()+",facet_"+SearchFacet.DATE_RANGE.getId();
			Map<String,String> params = new HashMap<String,String>();
			params.put("facets", facets);
			params.put("submit_save","Save");
			POST(CUSTOMIZE_FILTERS_URL,params);
			
			Response response = GET(LIST_URL);
			// Check that there are no filters.
			assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);
			
			// Narrow the field to 2011
			GET(FILTER_URL+"?action=add&type=rangeChoose&year=2011");
			response = GET(LIST_URL);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeStart\"\\s*>\\s*01/01/2011\\s*</a>",response);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeEnd\"\\s*>\\s*12/31/2011\\s*</a>",response);
	
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=0\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=11\""));
			
			// Narrow the field to May
			GET(FILTER_URL+"?action=add&type=rangeChoose&year=2011&month=4");
			response = GET(LIST_URL);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeStart\"\\s*>\\s*05/01/2011\\s*</a>",response);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeEnd\"\\s*>\\s*05/31/2011\\s*</a>",response);
	
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=0\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=11\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=1\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=11\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=21\""));
			
			
			// Narrow the field to days 11-20
			GET(FILTER_URL+"?action=add&type=rangeChoose&year=2011&month=4&days=11");
			response = GET(LIST_URL);
			assertContentMatch("filter\\?action=remove\\&type=rangeStart\"\\s*>\\s*05\\/11\\/2011\\s*</a>",response);
			assertContentMatch("filter\\?action=remove\\&type=rangeEnd\"\\s*>\\s*05\\/20\\/2011\\s*</a>",response);
	
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=0\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=1\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=21\""));
			
			// Remove the start date
			GET(FILTER_URL+"?action=remove&type=rangeStart");
			response = GET(LIST_URL);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeEnd\"\\s*>\\s*05/20/2011\\s*</a>",response);
			assertContentMatch("name=\"startDate\"",response);
			
			assertFalse(getContent(response).contains("filter?action=remove&type=rangeStart"));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=0\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=1\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=21\""));
			
			
			// Remove the end date
			GET(FILTER_URL+"?action=remove&type=rangeEnd");
			response = GET(LIST_URL);
			assertContentMatch("name=\"startDate\"",response);
			assertContentMatch("name=\"endDate\"",response);
	
			
			assertFalse(getContent(response).contains("filter?action=remove&type=rangeStart\""));
			assertTrue(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=0\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=1\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=11\""));
			assertFalse(getContent(response).contains("filter?action=add&type=rangeChoose&year=2011&month=4&days=21\""));
			
			
			// Add a manual start date
			params.clear();
			params.put("type", "range");
			params.put("action", "add");
			params.put("startDate", "5/5/2011");
			params.put("endDate", "");
			POST(FILTER_URL,params);
			response = GET(LIST_URL);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeStart\"\\s*>\\s*05/05/2011\\s*</a>",response);
			assertContentMatch("name=\"endDate\"",response);
	
			assertFalse(getContent(response).contains("name=\"startDate\""));
			
			// Add a manual end date
			params.clear();
			params.put("type", "range");
			params.put("action", "add");
			params.put("startDate", "");
			params.put("endDate", "5/7/2011");
			POST(FILTER_URL,params);
			response = GET(LIST_URL);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeStart\"\\s*>\\s*05/05/2011\\s*</a>",response);
			assertContentMatch("href=\"/admin/[^/]*/filter\\?action=remove&type=rangeEnd\"\\s*>\\s*05/07/2011\\s*</a>",response);
			
			assertFalse(getContent(response).contains("name=\"startDate\""));
			assertFalse(getContent(response).contains("name=\"endDate\""));
		}
	}
	
	/**
	 * Test that filters can be saved, cleared, and removed.
	 * 
	 * This will create a big filter with 2 of each type of parameter. Then it
	 * will save the filter. To test that it's actually saved and can be
	 * reloaded the active filter is cleared, followed by loading the previously
	 * saved filter. Everything is checked along the way, before the final step
	 * of deleting the filter.
	 */
	@Test
	public void testSaveClearAndRemoveFilter() throws InterruptedException {
		
		// Login as an administrator
		LOGIN();
		
		// Run for both the list and log tabs.
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
			
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			final String LIST_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
			
			Response response = GET(LIST_URL);
			// Check that there are no filters.
			assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);
			
			// Add Two of each filter type
			GET(FILTER_URL+"?action=add&type=state&value=Submitted");
			response = GET(LIST_URL);
	
			// Check that all the filters were added.
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=Submitted"));
			
			// Save the filter
			Map<String,String> params = new HashMap<String,String>();
			params.put("action", "save");
			params.put("name", "My Test Filter");
			params.put("public", "true");
			params.put("submit_save", "Save Filter");
			response = POST(FILTER_URL,params);
			
			// Extract the filter id so we don't depend it always being #1
			response = GET(LIST_URL);
			Pattern pattern = Pattern.compile("filter=(\\d+)\">My Test Filter<\\/a>");
			Matcher matcher = pattern.matcher(getContent(response));
			assertTrue(matcher.find());
			Long filterId = Long.valueOf(matcher.group(1));
			
			// Check that the filter was saved to the database.
			JPA.em().getTransaction().commit();
			JPA.em().clear();
			JPA.em().getTransaction().begin();
			NamedSearchFilter filter = subRepo.findSearchFilter(filterId);
			
			assertEquals("My Test Filter", filter.getName());
			assertEquals(1,filter.getStates().size());
			JPA.em().clear();
			
			// Clear the current filter
			GET(FILTER_URL+"?action=clear");
			response = GET(LIST_URL);
			assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);
	
			// Load the saved filter back into existance.
			GET(FILTER_URL+"?action=load&filter="+filterId);
			response = GET(LIST_URL);
			
			// Check that all the filters were loaded.
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=Submitted"));
			
			// Remove the filter
			params.clear();
			params.put("action","manage");
			params.put("remove",String.valueOf(filterId));
			params.put("submit_remove", "Remove Filters");
			POST(FILTER_URL,params);
			
			response = GET(LIST_URL);
			
			// Check that the filter has been removed
			assertFalse(getContent(response).contains("My Test Filter"));
			
			// Check that although the filter has been removed, the active filter has not been chnaged.
			assertTrue(getContent(response).contains("filter?action=remove&type=state&value=Submitted"));
	
			// Verify that the filter was removed in the database
			JPA.em().getTransaction().commit();
			JPA.em().clear();
			JPA.em().getTransaction().begin();
			filter =  subRepo.findSearchFilter(filterId);
			
			assertNull(filter);
		}
	}
	
	/**
	 * Test the submission display by changing sort orders and directions.
	 */
	@Test
	public void testSearchDisplay() {
		
		// Login as an administrator
		LOGIN();
		
		// Run for both the list and log tabs
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
			
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			final String LIST_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String SEARCH_URL = Router.reverse("FilterTab.modifySearch",routeArgs).url;
			
			Response response = null;
			
			// To save test run time instead of searching all possible search orders, we'll just check one.
			//for(SearchOrder order : SearchOrder.values()) {
				SearchOrder order = SearchOrder.ID;
			
			
				// Test each column as ascending and decending
				GET(SEARCH_URL+"?orderby="+order.getId());
				
	
				response = GET(LIST_URL);
				String labelName = Messages.get(nav.toUpperCase()+"_COLUMN_"+order.name());
				assertContentMatch("<th class=\"orderby selected ascending\">\\s*<a href=\"[^\"]*\\?direction=toggle\">"+labelName+"</a>",response);
				
				GET(SEARCH_URL+"?orderby="+order.getId());
				GET(SEARCH_URL+"?direction=toggle");
	
				response = GET(LIST_URL);
				assertContentMatch("<th class=\"orderby selected descending\">\\s*<a href=\"[^\"]*\\?direction=toggle\">"+labelName+"</a>",response);
				
				GET(SEARCH_URL+"?direction=toggle");
			//}
		}
	}
	
	/**
	 * Test customizing the columns displayed.
	 */
	@Test
	public void testCustomizeSearch() {
		
		// Login as an administrator
		LOGIN();
		
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
			
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			final String FILTER_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String CUSTOMIZE_URL = Router.reverse("FilterTab.customizeSearch",routeArgs).url;
			
			Response response = GET(FILTER_URL);
			
			assertContentMatch("customize-search-form",response);
			// The default tables shared between list and log tabs.
			assertContentMatch("<li id=\"column_"+SearchOrder.ID.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.STATE.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.ASSIGNEE.getId()+"\" class=\"originally-shown\"", response);
			
			// The default results per page.
			assertContentMatch("<option selected=\"true\" value=\"100\">100</option>",response);
			
			// Change the columns
			String columnsString = "column_"+SearchOrder.ID.getId()+",column_"+SearchOrder.DOCUMENT_TITLE.getId()+",column_"+SearchOrder.STATE.getId();
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("columns",columnsString);
			params.put("resultsPerPage", "20");
			params.put("submit_save","Save");
			POST(CUSTOMIZE_URL,params);
			
			response = GET(FILTER_URL);
			
			assertContentMatch("<li id=\"column_"+SearchOrder.ID.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.DOCUMENT_TITLE.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.STATE.getId()+"\" class=\"originally-shown\"", response);
			
			assertContentMatch("<li id=\"column_"+SearchOrder.ASSIGNEE.getId()+"\" class=\"originally-hidden\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.LAST_EVENT_ENTRY.getId()+"\" class=\"originally-hidden\"", response);
			assertContentMatch("<li id=\"column_"+SearchOrder.LAST_EVENT_TIME.getId()+"\" class=\"originally-hidden\"", response);
	
			assertContentMatch("<option selected=\"true\" value=\"20\">20</option>",response);
		}
	}
	
	/**
	 * Test changing the filter facet options
	 */
	@Test
	public void testCustomizeFacets() {
		// Login as an administrator
		LOGIN();
		
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {
		
			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);
			final String FILTER_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String CUSTOMIZE_URL = Router.reverse("FilterTab.customizeFilters",routeArgs).url;
			
			Response response = GET(FILTER_URL);
			
			assertContentMatch("filter-customize-modal",response);
			// The default facets shared between list and log tabs.
			assertContentMatch("<li id=\"facet_"+SearchFacet.TEXT.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.STATE.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.ASSIGNEE.getId()+"\" class=\"originally-shown\"", response);
			
			
			// Change the facets
			String facetsString = "facet_"+SearchFacet.TEXT.getId()+",facet_"+SearchFacet.DEGREE.getId()+",facet_"+SearchFacet.DATE_RANGE.getId();
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("facets",facetsString);
			params.put("submit_save","Save");
			POST(CUSTOMIZE_URL,params);
			
			response = GET(FILTER_URL);
			
			assertContentMatch("<li id=\"facet_"+SearchFacet.TEXT.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.DEGREE.getId()+"\" class=\"originally-shown\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.DATE_RANGE.getId()+"\" class=\"originally-shown\"", response);
			
			assertContentMatch("<li id=\"facet_"+SearchFacet.STATE.getId()+"\" class=\"originally-hidden\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.ASSIGNEE.getId()+"\" class=\"originally-hidden\"", response);
			assertContentMatch("<li id=\"facet_"+SearchFacet.UMI_RELEASE.getId()+"\" class=\"originally-hidden\"", response);
		}
	}
	
	/**
	 * Test that the filter options displayed for College, Department, and Major
	 * come from the list of those values actually in use.
	 * 
	 */
	@Test
	public void testCollegeDepartmentMajorList() {
		
		LOGIN();
		
		// Get the URLs
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("nav", "list");
		final String FILTER_URL = Router.reverse("FilterTab.list").url;
		final String CUSTOMIZE_URL = Router.reverse("FilterTab.customizeFilters",routeArgs).url;
		
		// Turn on the college, department, and major facets
		String facetsString = "facet_"+SearchFacet.COLLEGE.getId()+",facet_"+SearchFacet.DEPARTMENT.getId()+",facet_"+SearchFacet.MAJOR.getId();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("facets",facetsString);
		params.put("submit_save","Save");
		POST(CUSTOMIZE_URL,params);
				
		// Check that all the colleges are listed.
		Response response = GET(FILTER_URL);
		
		List<String> colleges = subRepo.findAllColleges();
		for (String college : colleges) {
			assertContentMatch("action=add\\&type=college\\&value=[^\"]*\">"+college.replaceAll("&", "\\&")+"<",response);
		}
		
		List<String> departments = subRepo.findAllDepartments();
		for (String department : departments) {
			assertContentMatch("action=add\\&type=department\\&value=[^\"]*\">"+department.replaceAll("&", "\\&")+"<",response);
		}
		
		List<String> majors = subRepo.findAllMajors();
		for (String major : majors) {
			assertContentMatch("action=add\\&type=major\\&value=[^\"]*\">"+major.replaceAll("&", "\\&")+"<",response);
		}

	}
	
	/**
	 * Test that we can reset the log filter to just search for all action logs
	 * from one particular submission.
	 */
	@Test
	public void testResetLogFilterToOneSubmission() {

		// Login as an administrator
		LOGIN();


		// Run for both the list and log tabs
		// Get our URLS
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("nav", "log");

		final String LIST_URL = Router.reverse("FilterTab.log",routeArgs).url;
		final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
		final String RESET_URL = Router.reverse("FilterTab.resetLogFilterToOneSubmission").url;


		Response response = GET(LIST_URL);
		// Check that there are no filters.
		assertContentMatch("<div class=\"main-heading\">Now filtering By:<\\/div>\\s*<\\/div>\\s*<div class=\"box-body\">\\s*<\\/div>", response);

		// Reset to one submission
		GET(RESET_URL+"?subId=5");

		// Check the list display
		response = GET(LIST_URL);
		assertTrue(getContent(response).contains("filter?action=remove&type=include_sub&value=5"));

		// Remove the submission
		GET(FILTER_URL+"?action=remove&type=sub&value=5");

		// Check the list display
		response = GET(LIST_URL);
		assertFalse(getContent(response).contains("filter?action=remove&type=sub&value=5"));
	}
	
	
	/**
	 * Test doing a batch status update
	 */
	@Test
	public void testBatchUpdate() throws InterruptedException {

		context.turnOffAuthorization();
		
		// Login as an administrator
		LOGIN();

		// Get our URLS
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("nav", "list");

		final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
		final String DEPOSIT_URL = Router.reverse("FilterTab.batchTransition").url;

		DepositLocation location = settingRepo.findAllDepositLocations().get(0);
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission sub = subRepo.createSubmission(person);
		sub.setDocumentTitle("Deposit test");
		sub.setDegree("Degree Deposit");
		sub.save();
		
		State publishState = null;
		for (State state : stateManager.getAllStates())
			if (state.isDepositable())
				publishState = state;
		
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		indexer.commit(true);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Filter for "Degree Deposit"
		GET(FILTER_URL+"?action=add&type=degree&value=Degree+Deposit");
		
		// Do the batch publish
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationId",String.valueOf(location.getId()));
		params.put("state",publishState.getBeanName());
		Response response = POST(DEPOSIT_URL,params);
		
		// Check that we were sent to the progress bar
		assertNotNull(response.getHeader("Location"));
		
		// Wait for the deposit to finish.
		jobManager.waitForJobs();
		
		// Check that the submission had a deposit id set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		sub = subRepo.findSubmission(sub.getId());
		assertNotNull(sub.getDepositId());
		sub.delete();
		
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test deleting a batch
	 */
	@Test
	public void testBatchDelete() throws InterruptedException {

		context.turnOffAuthorization();
		
		// Login as an administrator
		LOGIN();

		// Get our URLS
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("nav", "list");

		final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
		final String DELETE_URL = Router.reverse("FilterTab.batchTransition").url;

		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission sub = subRepo.createSubmission(person);
		sub.setDocumentTitle("Delete test");
		sub.setDegree("Degree Delete");
		sub.save();
		
		State deleteState = null;
		for (State state : stateManager.getAllStates())
			if (state.isDeletable())
				deleteState = state;
		
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		indexer.commit(true);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Filter for "Degree Deposit"
		GET(FILTER_URL+"?action=add&type=degree&value=Degree+Delete");
		
		// Do the batch publish
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationId","");
		params.put("state",deleteState.getBeanName());
		params.put("delete-submissions","confirm-delete");
		Response response = POST(DELETE_URL,params);
		
		// Check that we were sent to the progress bar
		assertNotNull(response.getHeader("Location"));
		
		// Wait for the deposit to finish.
		jobManager.waitForJobs();
		
		// Check that the submission had a deposit id set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		sub = subRepo.findSubmission(sub.getId());
		assertNull(sub);
		
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test downloading a batch export.
	 */
	@Test
	public void testBatchExport() {
		
		context.turnOffAuthorization();
		
		// Login as an administrator
		LOGIN();


		// Get our URLS
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("nav", "list");

		final String LIST_URL = Router.reverse("FilterTab.list").url;
		final String EXPORT_URL = Router.reverse("FilterTab.batchExport",routeArgs).url;
		
		// Double check the list
		Response response = GET(LIST_URL);
				
		assertContentMatch("id=\"batch-export-modal\"",response);
		
		// We can't actually test the export because the test apparatus dosn't support chunked responses.
//		Map<String,String> params = new HashMap<String,String>();
//		params.put("packager","DSpaceMETS");
//		response = POST(EXPORT_URL,params);
//		assertIsOk(response);
//		assertContentType("application/zip", response);
//		assertHeaderEquals("Content-Disposition", "attachment; filename=DSpaceMETS.zip", response);
	}
	
	/**
	 * Test that changing the search filter resets pagination back to the first
	 * page. This bug was reported in VIREO-89.
	 * 
	 * This test assumes that there will only be 10 submissions. It uses this to
	 * make adjustments to how far to paginate into the list.
	 */
	@Test
	public void testPaginationResets() {

		// Login as an administrator
		LOGIN();


		// Run for both the list and log tabs
		String[] possibleNavs = {"list","log"};
		for (String nav : possibleNavs) {

			// Get our URLS
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("nav", nav);

			final String LIST_URL = (nav.equals("list")) ? Router.reverse("FilterTab.list",routeArgs).url : Router.reverse("FilterTab.log",routeArgs).url;
			final String FILTER_URL = Router.reverse("FilterTab.modifyFilters",routeArgs).url;
			final String MODIFY_SEARCH_URL = Router.reverse("FilterTab.modifySearch",routeArgs).url;
			final String CUSTOMIZE_SEARCH_URL = Router.reverse("FilterTab.customizeSearch",routeArgs).url;

			
			// Change the number of results per page.
			String columnsString = "column_"+SearchOrder.ID.getId()+",column_"+SearchOrder.DOCUMENT_TITLE.getId()+",column_"+SearchOrder.STATE.getId();
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("columns",columnsString);
			params.put("resultsPerPage", "list".equals(nav) ? "2": "40");
			params.put("submit_save","Save");
			POST(CUSTOMIZE_SEARCH_URL,params);
			
			// With no filter in place, paginate to page 4 (there should be 5 total)
			GET(MODIFY_SEARCH_URL+"?offset=" +("list".equals(nav) ? "5": "120"));
			
			Response response = GET(LIST_URL);
			// Check that we have at least some submissions
			assertContentMatch("<a href=\"/admin/view\\?subId=\\d+\">\\d+</a>",response);
			
			
			// Change the filter status
			GET(FILTER_URL+"?action=add&type=state&value=InReview");
			
			
			response = GET(LIST_URL);
			// Check that our pagination was switched so that we show at least some records.
			assertContentMatch("<a href=\"/admin/view\\?subId=\\d+\">\\d+</a>",response);
			
		}

	}

}
