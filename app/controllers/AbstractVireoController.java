package controllers;

import java.util.Map;

import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * This is a common ancestor for all Vireo controllers. It will hold any common
 * code that is used by all controller methods. At the time of creation this
 * just mean loading all the major spring dependencies and injected them into
 * the view for templates to be able to access. However, it is expected that
 * additional things will be added to this class.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract class AbstractVireoController extends Controller {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	public static Searcher searcher = Spring.getBeanOfType(Searcher.class);

	
	/**
	 * This is run before any action to inject the repositories into the
	 * template. This way the template can access information from any
	 * repository without it being explicit put in for each method.
	 */
	@Before
	public static void injectRepositories() {
		renderArgs.put("securityContext",context);
		renderArgs.put("personRepo",personRepo);
		renderArgs.put("subRepo", subRepo);
		renderArgs.put("settingRepo", settingRepo);
		renderArgs.put("stateManager", stateManager);
		renderArgs.put("person",context.getPerson());
	}

	/**
	 * This is a helpfull debugging method so that developers can better
	 * understand the state of the applications while under initial development.
	 * 
	 * TODO: Remove this method before the end of the initial series of sprints.
	 */
	protected static void dumpParams() {

		Map<String, String> names = params.allSimple();

		Logger.info("Session: " + session.toString());

		Logger.info("Params:");
		
		for (Map.Entry<String, String> entry : names.entrySet())        {
			Logger.info(entry.getKey() + "= {" + entry.getValue() + "}");
		}
	}
}
