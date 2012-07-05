package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Field.Index;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.impl.LuceneIndexerImpl;
import org.tdl.vireo.services.EmailService;
import org.tdl.vireo.services.SystemEmailTemplateService;
import org.tdl.vireo.services.EmailService.TemplateParameters;

import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Router;
import play.mvc.With;
import play.mvc.Router.ActionDefinition;

/**
 * System administrator control panel.
 * 
 * This is a way for administrators to access system information for debugging
 * or testing. It is expected that this class will gain additional features and
 * information feeds in the future.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class System extends AbstractVireoController {

	// The email template to use when sending a test email.
	public static final String TEST_EMAIL_TEMPLATE = "SYSTEM Email Test";

	// Spring dependencies indjected.
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);
	public static SystemEmailTemplateService templateService = Spring.getBeanOfType(SystemEmailTemplateService.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	/**
	 * Redirect "system/" to "system/panel"
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void controlPanelRedirect() {
		controlPanel();
	}
	
	/**
	 * Display vital statistic information about vireo and allow access to some
	 * administrator features.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void controlPanel() {

		Runtime runtime = Runtime.getRuntime();	

		
		// Java Information
		String javaVersion = java.lang.System.getProperty("java.version");
		String osName = java.lang.System.getProperty("os.name");
		String osArch = java.lang.System.getProperty("os.arch");
		String osVersion = java.lang.System.getProperty("os.version");
		int availableProcessors = runtime.availableProcessors();
		
		// Runtime Memory Information
		long maximumMemory = runtime.maxMemory() / 1024 / 1024;
		long allocatedMemory = runtime.totalMemory() / 1024 / 1024;
		long freeMemory = runtime.freeMemory() / 1024 / 1024;
		long usedMemory = allocatedMemory - freeMemory / 1024 / 1024;

		// Play Information
		String playMode = Play.mode.name();
		String frameworkId = Play.id;
		Date startedAt = new Date(Play.startedAt);
		String httpAddress = Play.configuration.getProperty("http.address","n/a");
		String httpPort = Play.configuration.getProperty("http.port","n/a");
		String httpPath = Play.configuration.getProperty("http.path","n/a");
		String logLevel = Play.configuration.getProperty("application.log","n/a");
		String db = Play.configuration.getProperty("db","n/a");
		String appPath = Play.applicationPath.getPath();
		String attachmentsPath = Play.configuration.getProperty("attachments.path","n/a");
		String indexPath = Play.configuration.getProperty("index.path","n/a");


		// Mail Information
		String mailMode = Play.configuration.getProperty("mail.smtp","n/a");
		String mailHost = Play.configuration.getProperty("mail.smtp.host","n/a");
		String mailUser = Play.configuration.getProperty("mail.smtp.user","n/a");
		String mailPass = Play.configuration.getProperty("mail.smtp.pass");
		String mailChannel = Play.configuration.getProperty("mail.smtp.channel","n/a");
		String mailFrom = Play.configuration.getProperty("mail.from","n/a");
		String mailReply = Play.configuration.getProperty("mail.replyto","n/a");
		
		if (mailPass != null) {
			mailPass = mailPass.replaceAll(".", "*");
		} else {
			mailPass = "n/a";
		}
	
		// Index Information
		String indexImpl = indexer.getClass().getSimpleName();
		String indexJob = "n/a";
		if (indexer.isJobRunning()) {
			indexJob = indexer.getCurrentJobLabel();
			
			float complete = indexer.getCurrentJobProgress() / indexer.getCurrentJobTotal();
			indexJob += " ("+complete+"% complete)";
		}

		render(
				// Java Info
				javaVersion, osName, osArch, osVersion, availableProcessors,
				
				// Memory Info
				maximumMemory, allocatedMemory, freeMemory, usedMemory,
				
				// Play Info
				playMode, frameworkId, httpAddress, httpPort, httpPath, startedAt, appPath, logLevel, db, attachmentsPath, indexPath,
				
				// Mail Info
				mailMode, mailHost, mailUser, mailPass, mailChannel, mailFrom, mailReply,
				
				// Index Information
				indexImpl, indexJob
				
				);
	}
	
	/**
	 * Send a test email to the provided address.
	 * 
	 * @param email The email address to send the test.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void testEmail(String email) {
		
		templateService.generateAllSystemEmailTemplates();
		EmailTemplate template = settingRepo.findEmailTemplateByName(TEST_EMAIL_TEMPLATE);
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(email);
		emailService.sendEmail(template, new TemplateParameters(), recipients, null);
		
		render();
	}
	
	/**
	 * Rebuild the index, either destructively or in place.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void rebuildIndex() {
		
		if (params.get("submit_rebuild") != null) {
			indexer.rebuild(false);
		} if (params.get("submit_deleteAndRebuild") != null) {
			indexer.deleteAndRebuild(false);
		}
		
		// Redirect back to the control pannel.
		controlPanel();
	}
	
	
	
	
}
