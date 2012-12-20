package controllers;

import org.apache.commons.lang.StringEscapeUtils;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.Before;
import play.mvc.Catch;
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
	public static ProquestVocabularyRepository proquestRepo = Spring.getBeanOfType(ProquestVocabularyRepository.class);
	public static ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	public static Searcher searcher = Spring.getBeanOfType(Searcher.class);

	public static Boolean firstUser = null;
	
	/**
	 * This is run before any action to inject the repositories into the
	 * template. This way the template can access information from any
	 * repository without it being explicit put in for each method.
	 */
	@Before
	public static void injectRepositories() {
		renderArgs.put("securityContext", context);
		renderArgs.put("personRepo", personRepo);
		renderArgs.put("subRepo", subRepo);
		renderArgs.put("settingRepo", settingRepo);
		renderArgs.put("proquestRepo", proquestRepo);
		renderArgs.put("stateManager", stateManager);
		renderArgs.put("indexer", indexer);
	}
	
	@Before(unless = { "FirstUser.createUser" })
	public static void checkForFirstUser() {
		if(firstUser==null) {
			if(personRepo.findPersonsTotal()==0) {
				firstUser = true;				
			} else {
				firstUser = false;
			}
		}
		if(firstUser==true)
			FirstUser.createUser();
	}
	
	/**
	 * Catch any errors and report them to the log for review under the systems
	 * control panel.
	 * 
	 * @param throwable
	 *            The error encountered
	 */
	@Catch(Throwable.class)
	public static void handleError(Throwable throwable) {
		errorLog.logError(throwable, request);
	}
	
	
	
	/**
	 * Return true if the string is either null or only consists of whitespace.
	 * 
	 * @param value
	 *            The string to test.
	 * @return True if the value is empty, otherwise false.
	 */
	protected static boolean isEmpty(String value) {
		if (value == null)
			return true;
		if (value.trim().length() == 0)
			return true;
		return false;
	}
	
	/**
	 * Return true if the string contains a textual value and not just whitespace.
	 * 
	 * @param value
	 *            The string to test.
	 * @return True if the value has text, otherwise false.
	 */
	protected static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
	
	/**
	 * Escape Javascrip strings. In Javascript double quotes must be escaped.
	 * 
	 * @param value
	 *            The java string.
	 * @return A javascript escaped string
	 */
	protected static String escapeJavaScript(String value) {
		if (value == null)
			return "";
		value = StringEscapeUtils.escapeJavaScript(value);
		value = value.replaceAll("\\\\'", "'");
		return value;
	}
	
	/**
	 * Convert a simple java array of strings into a JSON object.
	 * 
	 * @param values
	 *            The array to be encoded as JSON.
	 * @return A json string.
	 */
	protected static String toJSON(String[] values) {
		
		if (values == null)
			return "[]";
		
		String result = "[";
		for (String value: values) {
			if (result.length() > 1) {
				result += ",";
			}
			result += "\""+escapeJavaScript(value)+"\"";
		}
		
		result += "]";
		
		return result;
	}
	
	/**
	 * Convert plain text into passable HTML. Separate text into paragraphs,
	 * preserve intending, and try not to mess with any embedded tags.
	 * 
	 * @param value
	 *            The input text.
	 * @return HTML suitable for display.
	 */
	protected static String text2html(String value) {

		String html = value.replaceAll("  ", "&nbsp;&nbsp;");
		String[] paragraphs = html.split("\n\\s*\n");
		html = "";
		for (String paragraph : paragraphs) {
			html += "<p>" + paragraph + "</p>";
		}

		html = html.replaceAll("\n", "<br/>");

		return html;
	}
	
	/**
	 * Return whether the specified field is required. If it is then return
	 * true, otherwise false.
	 * 
	 * @param field
	 *            The field to check whether it is required.
	 * @return True if required, otherwise false.
	 */
	protected static boolean isFieldRequired(FieldConfig field) {
		return "required".equals(settingRepo.getConfigValue(field.ENABLED));
	}
}
