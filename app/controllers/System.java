package controllers;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.SystemEmailTemplateService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.error.ErrorReport;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.search.Indexer;

import play.Play;
import play.jobs.Job;
import play.modules.spring.Spring;
import play.mvc.With;

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
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);

	
	/**
	 * Redirect "system/" to "system/general" so it defaults to the general panel.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void controlPanelRedirect() {
		generalPanel();
	}
	
	/**
	 * Display vital statistic information about vireo and allow access to some
	 * administrator features.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void generalPanel() {

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
		
		// Vireo Information
		String vireoVersion = Play.configuration.getProperty("vireo.version","Unknown");
		long personTotal = personRepo.findPersonsTotal();
		long submissionTotal = subRepo.findSubmissionsTotal();
		long actionLogTotal = subRepo.findActionLogsTotal();
		
	
		// Index Information
		String indexImpl = indexer.getClass().getSimpleName();
		String indexJob = "n/a";
		if (indexer.isJobRunning()) {
			indexJob = indexer.getCurrentJobLabel();
			float complete = 0;
			if (indexer.getCurrentJobTotal() > 0)
				complete = (((float) indexer.getCurrentJobProgress()) / indexer.getCurrentJobTotal()) * 100;
						
			indexJob += String.format(" ( %.3f%% complete )",complete);
		}


		renderTemplate("System/generalPanel.html",
				// Java Info
				javaVersion, osName, osArch, osVersion, availableProcessors,
				
				// Memory Info
				maximumMemory, allocatedMemory, freeMemory, usedMemory,
				
				// Play Info
				playMode, frameworkId, httpAddress, httpPort, httpPath, startedAt, appPath, logLevel, db, attachmentsPath, indexPath,
				
				// Mail Info
				mailMode, mailHost, mailUser, mailPass, mailChannel, mailFrom, mailReply,
				
				// Vireo Info
				vireoVersion, personTotal, submissionTotal, actionLogTotal,
				
				// Index Information
				indexImpl, indexJob
				
				);
	}
	
	/**
	 * Display and manage background jobs.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void jobPanel() {
	
		// Job data
		List<JobMetadata> jobs = jobManager.findAllJobs();
		
		// Status Definitions
		for (JobStatus status : JobStatus.values()) {
			renderArgs.put(status.name(), status);
		}
		
		renderTemplate("System/jobPanel.html",jobs);
	}
	
	/**
	 * Display recent errors.
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void errorPanel() {
	
		List<ErrorReport> reports = errorLog.getRecentErrorReports();
		
		renderTemplate("System/errorPanel.html",reports);
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
		
		
		VireoEmail vireoEmail = emailService.createEmail();
		vireoEmail.addTo(email);
		vireoEmail.setTemplate(template);
		emailService.sendEmail(vireoEmail,true);
		
		render();
	}
	
	/**
	 * Generate test submissions.
	 * 
	 * @param howMany
	 *            How many test submissions to generate.
	 * @throws InterruptedException
	 */
	@Security(RoleType.ADMINISTRATOR)
	public static void testSubmissions(final int howMany) throws InterruptedException {
				
		if (!Play.mode.isDev())
			error("Generating test submissions is only available in a DEV mode.");
		
		if (!"test".equals(Play.id))
			error("Generating test submissions is only available when runing under the '%test' framework id.");
		
		if (howMany < 1)
			error();
	
		long start = java.lang.System.currentTimeMillis();
		Job job = new TestSubmissionsJob(indexer,howMany);
		await(job.now());
		long stop = java.lang.System.currentTimeMillis();
		
		long totalTime = stop - start;
		long timePerSubmission = totalTime / howMany;

		renderTemplate("System/testSubmission.html", howMany, totalTime, timePerSubmission);
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
		generalPanel();
	}
	
	/**
	 * Background job to generate random submissions.
	 */
	public static class TestSubmissionsJob extends Job {
		
		public Indexer indexer;
		public int howMany;
		
		/**
		 * Construct a new job.
		 * 
		 * @param indexer
		 *            The indexer, we will rebuild the entire index after
		 *            completing.
		 * @param howMany
		 *            How many random submissions to generate.
		 */
		public TestSubmissionsJob(Indexer indexer, int howMany) {
			this.indexer = indexer;
			this.howMany = howMany;
		}
		
		/**
		 * Actually do the job of random submissions.
		 * 
		 * Important note we don't actually import the TestDataLoader and call
		 * the loadSubmissions method directly because we don't want a compile
		 * time dependency between production code and test code. This feature
		 * is only available when running under a test environment.
		 */
		public void doJob() throws Exception {
			
			try {
				Class clazz = Class.forName("TestDataLoader");
				
				Class[] types = { long.class, int.class };
				Method method = clazz.getMethod("loadSubmissions", types);
				
				long seed = java.lang.System.currentTimeMillis();
				method.invoke(null, seed, howMany);
				
				indexer.rollback();
				indexer.rebuild(false);
			} catch (Exception e) {
				errorLog.logError(e, "Loading test submissions");
			}
		}
	}
	
}
