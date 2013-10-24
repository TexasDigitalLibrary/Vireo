import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.security.auth.Subject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.email.SystemEmailTemplateService;
import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.export.impl.FileDepositorImpl;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.proquest.ProquestSubject;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.search.impl.LuceneIndexerImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.security.impl.ShibbolethAuthenticationMethodImpl;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import controllers.settings.ThemeSettingsTab;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.spring.Spring;

import static org.tdl.vireo.constant.AppConfig.*;


/**
 * When running in a test environment pre-load some configuration and test data
 * just to make things easier.
 * 
 * 
 * @author Micah Cooper
 * @author <a herf="www.scottphillips.com">Scott Phillips</a>
 * 
 */
@OnApplicationStart
public class TestDataLoader extends Job {

	
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static ProquestVocabularyRepository proquestRepo = Spring.getBeanOfType(ProquestVocabularyRepository.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static LuceneIndexerImpl indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
	public static ShibbolethAuthenticationMethodImpl shibAuth = Spring.getBeanOfType(ShibbolethAuthenticationMethodImpl.class);
	public static SystemEmailTemplateService systemEmailService = Spring.getBeanOfType(SystemEmailTemplateService.class);
		
	/**
	 * How many random submissions to create
	 */
	public static final int RANDOM_SUBMISSIONS = 10;	
	
	/**
	 * Initial Persons to create
	 */
	private static final PersonsArray[] PERSONS_DEFINITIONS = {
		new PersonsArray("000000001", "bthornton@gmail.com", "Billy", "Thornton", "password",  RoleType.ADMINISTRATOR),
		new PersonsArray("000000002", "mdriver@gmail.com", "Minnie", "Driver", "password", RoleType.MANAGER),
		new PersonsArray("000000003", "jdimaggio@gmail.com", "John", "Di Maggio", "password", RoleType.REVIEWER),
		new PersonsArray("000000004", "cdanes@gmail.com", "Claire", "Danes", "password", RoleType.STUDENT),
		new PersonsArray("000000005", "bcrudup@gmail.com", "Billy", "Crudup", "password", RoleType.STUDENT),
		new PersonsArray("000000006", "ganderson@gmail.com", "Gillian", "Anderson", "password", RoleType.STUDENT)
	};
	
	/**
	 * Initial configuration
	 */
	private static final ConfigurationArray[] CONFIG_DEFINITIONS = {
		
		new ConfigurationArray(SUBMISSIONS_OPEN, "true"),
		new ConfigurationArray(ALLOW_MULTIPLE_SUBMISSIONS, "true"),
		new ConfigurationArray(CURRENT_SEMESTER, "May 2012"),
		new ConfigurationArray(GRANTOR, "Texas A&M University"),
		
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Doctor of Philosophy"), "Ph.D."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Doctor of Engineering"), "D.Eng."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Doctor of Education"), "Ed.D."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Doctor of Musical Arts"), "D.M.A."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Arts"), "M.A."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Landscape Architecture"), "MLA"),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Marine Resources Management"), "MMRM"),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Public Affairs"), "M.P.Aff."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Science"), "M.Sc."),
		new ConfigurationArray(AppConfig.getDegreeCodeConfig("Master of Urban Planning"), "MUP"),
	};
	
	/**
	 * Initial Programs to create
	 */
	private static final String[] PROGRAMS_DEFINITIONS = {
		"Graduate Studies",
		"Undergraduate Honors Fellows",
		"Undergraduate Scholars"
	};
	
	/**
	 * Initial Colleges to create
	 */
	private static final String[] COLLEGES_DEFINITIONS = {
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Dwight Look College of Engineering",
		"Interdisciplinary Degree Programs",
		"Mays Business School",
		"Texas A&M University at Galveston",
		"Texas A&M University at Qatar"
	};
	
	/**
	 * Initial Departments to create
	 */
	
	private static final String[] DEPARTMENTS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Animal Science",
		"Anthropology",
		"Architecture",
		"Atmospheric Sciences",
		"Biochemistry and Biophysics",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Engineering",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Communication",
		"Computer Science and Engineering",
		"Construction Science",
		"Economics",
		"Ecosystem Science and Management",
		"Educational Administration and Human Resource Development",
		"Educational Psychology",
		"Electrical and Computer Engineering",
		"English",
		"Entomology",
		"Finance",
		"Geography",
		"Geology and Geophysics",
		"Health and Kinesiology",
		"Hispanic Studies",
		"History",
		"Horticultural Sciences",
		"Industrial and Systems Engineering",
		"Information and Operations Management",
		"Landscape Architecture and Urban Planning",
		"Management",
		"Marine Biology",
		"Marine Sciences",
		"Marketing",
		"Mathematics",
		"Mays Business School",
		"Mechanical Engineering",
		"Nuclear Engineering",
		"Nutrition and Food Science",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy and Humanities",
		"Physics and Astronomy",
		"Plant Pathology and Microbiology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Recreation, Park, and Tourism Sciences",
		"Sociology",
		"Soil and Crop Sciences",
		"Statistics",
		"Teaching, Learning, and Culture",
		"Veterinary Integrative Biosciences",
		"Veterinary Large Animal Clinical Sciences",
		"Veterinary Pathobiology",
		"Veterinary Physiology and Pharmacology",
		"Veterinary Small Animal Clinical Sciences",
		"Visualization",
		"Wildlife and Fisheries Sciences"
	};
	
	/**
	 * Initial Majors to create
	 */
	
	private static final String[] MAJORS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agribusiness",
		"Agribusiness and Managerial Economics",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Agricultural Systems Management",
		"Agronomy",
		"Animal Breeding",
		"Animal Science",
		"Anthropology",
		"Applied Physics",
		"Architecture",
		"Atmospheric Sciences",
		"Bilingual Education",
		"Biochemistry",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Biomedical Sciences",
		"Biotechnology",
		"Botany",
		"Business Administration",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"Communication",
		"Comparative Literature and Culture",
		"Computer Engineering",
		"Computer Science",
		"Construction Management",
		"Counseling Psychology",
		"Curriculum and Instruction",
		"Dairy Science",
		"Economics",
		"Educational Administration",
		"Educational Human Resource Development",
		"Educational Psychology",
		"Electrical Engineering",
		"Engineering",
		"Engineering Systems Management",
		"English",
		"Entomology",
		"Epidemiology",
		"Finance",
		"Floriculture",
		"Food Science and Technology",
		"Forestry",
		"Genetics",
		"Geography",
		"Geology",
		"Geophysics",
		"Health Education",
		"Health Physics",
		"Hispanic Studies",
		"History",
		"Horticulture",
		"Industrial Engineering",
		"Interdisciplinary Engineering",
		"Kinesiology",
		"Laboratory Animal Medicine",
		"Management",
		"Management Information Systems",
		"Marine Biology",
		"Marine Resources Management",
		"Marketing",
		"Materials Science and Engineering",
		"Mathematics",
		"Mechanical Engineering",
		"Microbiology",
		"Modern Languages",
		"Molecular and Environmental Plant Sciences",
		"Nuclear Engineering",
		"Nutrition",
		"Ocean Engineering",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy",
		"Physical Education",
		"Physics",
		"Physiology of Reproduction",
		"Plant Breeding",
		"Plant Pathology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Rangeland Ecology and Management",
		"Recreation, Park, and Tourism Sciences",
		"Safety Engineering",
		"School Psychology",
		"Science and Technology Journalism",
		"Sociology",
		"Soil Science",
		"Sport Management",
		"Statistics",
		"Toxicology",
		"University Studies",
		"Urban and Regional Planning",
		"Urban and Regional Sciences",
		"Veterinary Anatomy",
		"Veterinary Microbiology",
		"Veterinary Pathology",
		"Veterinary Medical Sciences",
		"Veterinary Parasitology",
		"Veterinary Physiology",
		"Veterinary Public Health",
		"Visualization Sciences",
		"Water Management and Hydrological Science",
		"Wildlife and Fisheries Sciences",
		"Wildlife Science",
		"Zoology"
	};
	
	/**
	 * Initial Degrees to create
	 */

	private static final DegreeLevelArray[] DEGREES_DEFINITIONS = {
		new DegreeLevelArray("Doctor of Philosophy", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Engineering", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Education", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Musical Arts", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Master of Arts", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Landscape Architecture", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Marine Resources Management", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Public Affairs", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Science", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Urban Planning", DegreeLevel.MASTERS),
		new DegreeLevelArray("Bachelor of Arts", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Science", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Environmental Design", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Document Types to create
	 */
	
	private static final DegreeLevelArray[] DOCTYPES_DEFINITIONS = {
		new DegreeLevelArray("Record of Study", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Dissertation", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Thesis", DegreeLevel.MASTERS),
		new DegreeLevelArray("Thesis", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Committee Member Role Types to create
	 */
	
	private static final DegreeLevelArray[] ROLETYPES_DEFINITIONS = {
		new DegreeLevelArray("Chair", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Co-Chair", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Director of Research", DegreeLevel.DOCTORAL),

		new DegreeLevelArray("Chair", DegreeLevel.MASTERS)
	};
	
	/**
	 * Initial Graduation Months to create
	 */
	
	private static final int[] GRAD_MONTHS_DEFINITIONS = {
		4, 7, 11 // 0 = january, 11 = december
	};
	
	/**
	 * Initial Embargo Types to create
	 */
	
	private static final EmbargoArray[] EMBARGO_DEFINTITIONS = {
		new EmbargoArray("None", "The work will be published after approval.", 0, true),
		new EmbargoArray("Journal Hold",
				"The full text of this work will be held/restricted from worldwide access on the internet for one year from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication. For doctoral students, the abstract of the work will be available through ProQuest/UMI during this time.", 
				12,
				true),
		new EmbargoArray("Patent Hold",
				"The full text of this work will be held/restricted from public access temporarily because of patent related activities or for proprietary purposes. The faculty chair will be contacted on an annual basis, and the work will be released following the chair's approval.",
				24,
				true
				),
	    new EmbargoArray("Other Embargo Period",
	    		"The work will be delayed for publication by an indefinite amount of time.",
	    		null,
	    		false),
	    new EmbargoArray("2-year Journal Hold",
	    		"The full text of this work will be held/restricted from worldwide access on the internet for two years from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication. The abstract of the work will be available through Texas A&M Libraries and, for doctoral students, through ProQuest/UMI during this time.",
	    		null,
	    		true)
	};
	
	/**
	 * Initial custom actions
	 */
	private static final String[] CUSTOM_ACTIONS_DEFINITIONS = {
		"Apply for Graduation",
		"Register for Semester",
		"Verify Passing/Exemption of Oral Exam",
		"Approval Form",
		"Submit All Copyright Permissions",
		"C&A Form",
		"ProQuest",
		"Survey of Earned Doctorates",
		"AAUDE Survey",
		"Sent to UMI/ProQuest"
	};

	/**
	 * Initial Email Templates 
	 */
	private static final EmailTemplateArray[] EMAIL_TEMPLATE_DEFINITIONS = {
		new EmailTemplateArray(
				"E-Submittal Complete - No Signed Approval form", 
				"[ETD] Signed approval forms needed", 
				"{FIRST_NAME} {LAST_NAME}:\n"+
				"\n"+
				"You have successfully finished the ONLINE portion of submitting your manuscript. However, submittal is NOT COMPLETE until we receive your signed Approval Form. Once received, we will begin the review of your manuscript.\n"+
				"\n"+
				"If you think that we should already have received your Approval Form, please contact the Thesis Office.\n"+
				"\n"+
				"Thank you,\n"+
				"\n"+
				"The Vireo Team\n"),
		new EmailTemplateArray(
				"First Round Manuscript Corrects Ready for Download",
				"[ETD] First round corrects ready",
				"{FIRST_NAME},\n"+
				"\n"+
				"The review of your manuscript is complete. The list of remaining corrections can be viewed or downloaded by logging in to the submittal system.\n"+ 
				"\n"+
				"When you log into the system, you should click \"View\" under the Actions heading. At the bottom of this page you should see a box labeled Application Activity. If you scroll down within that box, you should see a link to your corrections.\n"+
				"\n"+
				"Student Link: {STUDENT_URL}\n"+
				"\n"+
				"Carefully make the requested corrections and submit your revised file as soon as possible. After you make the changes to your original file, convert it to PDF. Click the \"Replace Thesis\" button next to your currently uploaded file, and then click \"Browse\" to find and upload the new version. You may add comments by typing them in the message box and clicking \"Add Message.\" Once you have finished, click the \"Submit Corrections\" button at the bottom of the page. Clicking this button will finalize your re-submission. You will not be able to make further changes.\n"+
				"\n"+
				"All requirements must be met for the Thesis Office to clear your record. Once you are cleared, the Thesis Office will send you a confirmation email. If you have any questions or concerns, please contact our office.\n"+
				"\n"+
				"Your advisory chair should use the following link to view this submission: {ADVISOR_URL}\n"+
				"\n"+
				"Thank you,\n"+
				"\n"+
				"The Vireo Team\n"),
		new EmailTemplateArray(
				"Apply for Graduation Reminder",
				"[ETD] Apply for Graduation", 
				"{FIRST_NAME},\n"+
				"\n"+
				"In order to clear the Thesis Office, you need to apply for graduation in the semester you will be officially graduating (http://howdy.tamu.edu). As of today, our records indicate that you have not yet applied for graduation. The last day to apply is November 27. If you still plan to graduate in December please complete this requirement as soon as possible. If you have questions or concerns, please contact our office. \n"+
				"\n"+
				"Thank you,\n"+
				"\n"+
				"The Vireo Team\n")
	};
		
	/**
	 * Initial deposit locations
	 */
	
	private static final DepositLocationArray[] DEPOSIT_LOCATION_DEFINITIONS = {
		new DepositLocationArray(
				"Mock Sword Repository", // name
				"http://localhost:8082/servicedocument", // repository
				"http://localhost:8082/deposit/a", // collection
				"testUser", // username
				"testPassword", // password
				null, // onBehalfOf
				"DSpaceMETS", 
				"Sword1Deposit"),
		new DepositLocationArray(
				"File Deposit", // name
				"data/deposits/", // repository
				"", // collection
				"", // username
				"", // password
				"", // onBehalfOf
				"DSpaceMETS", 
				"FileDeposit")
	};
	
	/**
	 * Initial languages
	 */
	private static final String[] LANGUAGES_DEFINITIONS = {
		"en",
		"es",
		"fr"
	};
	
	/**
	 * This is the driver method which will call the three types of load methods
	 * individually, for People, Settings, and Submissions.
	 */
	@Override
	public void doJob() {
		try {			
			// Turn off authorizations.
			context.turnOffAuthorization(); 
			
			// Check to see if test data has already been loaded.
			if (settingRepo.getConfigBoolean("TEST_DATA_LOADER")) {
				Logger.debug("Test data has already been loaded, skiping...");
				return;
			}
			Logger.debug("Loading test data.");
			settingRepo.createConfiguration("TEST_DATA_LOADER", "true").save();
			
			try {
				// Clean out directories
				File attachmentsDir = new File(Play.configuration.getProperty("attachments.path"));
				if (attachmentsDir.exists())
					FileUtils.deleteQuietly(attachmentsDir);
				
				File indexDir = new File(Play.configuration.getProperty("index.path"));
				if (indexDir.exists())
					FileUtils.deleteQuietly(indexDir);
				
				File depositsDir = new File(Play.configuration.getProperty("deposits.path"));
				if (depositsDir.exists())
					FileUtils.deleteQuietly(depositsDir);
				
				File leftLogo = new File(ThemeSettingsTab.LEFT_LOGO_PATH);
				if(leftLogo.exists())
					leftLogo.delete();
				
				File rightLogo = new File(ThemeSettingsTab.RIGHT_LOGO_PATH);
				if(rightLogo.exists())
					rightLogo.delete();
				
				loadPeople();
				loadSettings();
			} finally {
				context.restoreAuthorization();
			}
			
			loadSubmissions(123456789,RANDOM_SUBMISSIONS);
			
			Logger.debug("Rebuilding index...");
			indexer.deleteAndRebuild(true);
			indexer.rollback();
			
			context.logout();
			
		} catch (Throwable t) {Logger.error(t, "Unable to load test data.");}
	}
	
	/**
	 * Load the predefined user accounts.
	 * 
	 * There is also one special case, so that Billy Bob Thorton is logged in
	 * through shibboleth so that all his attributes will be the same as defined
	 * by our mock shibboleth provider.
	 */
	public static void loadPeople() {

		// Create all persons
		for(PersonsArray personDefinition : PERSONS_DEFINITIONS) {
			Person person = personRepo.createPerson(personDefinition.netId, personDefinition.email, personDefinition.firstName, personDefinition.lastName, personDefinition.role).save();
			person.setPassword(personDefinition.password);
			person.save();
		}
		// Special case. Initialize Billy-bob with all the data defined by the shibboleth authentication. This results in a lot less confusion when the authentitation changes a person's metadat.
		
		boolean originalMock = shibAuth.mock;
		shibAuth.mock = true;
		shibAuth.authenticate(null);
		shibAuth.mock = originalMock;
		
	}
	
	/**
	 * Load all predefined settings. Colleges, departments, majors, degrees,
	 * document types, graduation month, and embargo definitions.
	 */
	public static void loadSettings() throws IOException {
				
		// Create all configuration settings
		for (ConfigurationArray config : CONFIG_DEFINITIONS) {
			settingRepo.createConfiguration(config.name, config.value).save();
		}
		
		// Create all programs
		for(String programDefinition : PROGRAMS_DEFINITIONS) {
			settingRepo.createProgram(programDefinition).save();
		}
		
		// Create all colleges
		for(String collegeDefinition : COLLEGES_DEFINITIONS) {
			settingRepo.createCollege(collegeDefinition).save();
		}
		
		// Create all departments
		for(String departmentDefinition : DEPARTMENTS_DEFINITIONS) {
			settingRepo.createDepartment(departmentDefinition).save();
		}
		
		// Create all majors
		for(String majorDefinition : MAJORS_DEFINITIONS) {
			settingRepo.createMajor(majorDefinition).save();
		}
		
		// Create all degrees
		for(DegreeLevelArray degreeDefinition : DEGREES_DEFINITIONS) {
			settingRepo.createDegree(degreeDefinition.name, degreeDefinition.degreeLevel).save();
		}
		
		// Create all document types
		for(DegreeLevelArray docTypeDefinition : DOCTYPES_DEFINITIONS) {
			settingRepo.createDocumentType(docTypeDefinition.name, docTypeDefinition.degreeLevel).save();
		}
		
		// Create all committee member role types
		for(DegreeLevelArray roleTypeDefinition : ROLETYPES_DEFINITIONS) {
			settingRepo.createCommitteeMemberRoleType(roleTypeDefinition.name, roleTypeDefinition.degreeLevel).save();
		}
		
		// Create all graduation months
		for(int gradMonthDefinition : GRAD_MONTHS_DEFINITIONS) {
			settingRepo.createGraduationMonth(gradMonthDefinition).save();
		}
		
		// Create all embargo types
		for(EmbargoArray embargoDefinition : EMBARGO_DEFINTITIONS) {
			settingRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active).save();
		}
		
		// Create all custom actions
		for(String actionDefinition : CUSTOM_ACTIONS_DEFINITIONS) {
			settingRepo.createCustomActionDefinition(actionDefinition).save();
		}
		
		// Create all email templates
		systemEmailService.generateAllSystemEmailTemplates();
		for(EmailTemplateArray templateDefinition : EMAIL_TEMPLATE_DEFINITIONS) {
			settingRepo.createEmailTemplate(templateDefinition.name, templateDefinition.subject, templateDefinition.message).save();
		}
		
		// Create all deposit locations
		for(DepositLocationArray locationDefinition : DEPOSIT_LOCATION_DEFINITIONS) {
			Depositor depositor = (Depositor) Spring.getBean(locationDefinition.depositor);
			Packager packager = (Packager) Spring.getBean(locationDefinition.packager);
			
			DepositLocation location = settingRepo.createDepositLocation(locationDefinition.name);
			location.setRepository(locationDefinition.repository);
			location.setCollection(locationDefinition.collection);
			location.setUsername(locationDefinition.username);
			location.setPassword(locationDefinition.password);
			location.setOnBehalfOf(locationDefinition.onBehalfOf);
			location.setDepositor(depositor);
			location.setPackager(packager);
			
			
			if (depositor instanceof FileDepositorImpl) {
				File baseDir = ((FileDepositorImpl) depositor).baseDir;
				FileUtils.forceMkdir(baseDir);
				
				location.setCollection(new File(baseDir.getCanonicalPath()+File.separator+locationDefinition.collection).getCanonicalPath());
			}
			
			location.save();
		}
		
		// Create all languages
		for(String languageDefinition : LANGUAGES_DEFINITIONS) {
			settingRepo.createLanguage(languageDefinition).save();
		}
	}
	
	/**
	 * Load randomly generated submissions.
	 */
	public static void loadSubmissions(long seed, int howMany) throws IOException {
		
		// Cache a list of all embargo types.
		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		// Cache the person who will generate action logs for all items.
		Person reviewer = personRepo.findPersonByEmail("jdimaggio@gmail.com");
		
		// Establish a constant random seed so each run through this code produces the same results.
		Random random = new Random(seed);
		
		long start = System.currentTimeMillis();
		for(int i=0; i < howMany; i++) {
			context.turnOffAuthorization();			
			Person student = personRepo.findPersonByNetId("student"+i);
			if (student == null) {
				String[] studentName = generateRandomName(random, ACTOR_NAMES);				
				String studentEmail = generateRandomEmail(random, studentName);
				studentEmail = studentEmail.replaceFirst("@", (i+1)+"@");
				
				student = personRepo.createPerson("student"+i, studentEmail, studentName[0], studentName[1], RoleType.STUDENT).save();
				if (i > 0) {
					student.setMiddleName(studentName[2]);
					student.setPassword("password");
					student.setCurrentPhoneNumber("555-555-5555");
					student.setCurrentPostalAddress("2335 Barron Basen Dr\nSome Town, Texas 77845");
					student.setPermanentPhoneNumber("555-999-9999");
					student.setPermanentPostalAddress("2335 Dry Gulch Dr\nAnother Town, IL 78834");
					student.setPermanentEmailAddress("permanent@gmail.com");
					student.setInstitutionalIdentifier(
							"" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) +
							random.nextInt(9) + random.nextInt(9) + random.nextInt(9) +
							random.nextInt(9) + random.nextInt(9) +random.nextInt(9)
							);
					if(random.nextInt(100) > 50)
						student.setOrcid(
								"" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) +
								"-" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) +
								"-" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) +
								"-" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9)
								);
				}
				student.save();
			}
			
			String[] studentName = new String[3];
			studentName[0] = student.getFirstName();
			studentName[1] = student.getLastName();
			studentName[2] = student.getMiddleName();
			
			context.restoreAuthorization();
			context.login(student);
			Submission sub = subRepo.createSubmission(student).save();

			if(i>0){
				sub.setStudentFirstName(studentName[0]);
				sub.setStudentLastName(studentName[1]);
				sub.setStudentMiddleName(studentName[2]);
				if (random.nextInt(100) > 30)
					sub.setStudentBirthYear(random.nextInt(20)+1980);
				
				if (student.getOrcid()!=null)
					sub.setOrcid(student.getOrcid());
				
				if (random.nextInt(100) > 5)
					sub.setEmbargoType(embargos.get(random.nextInt(embargos.size()-1)));
				
				int members = random.nextInt(5);
				String[] firstMemberName = null;
				for (int m = 0 ; m < members; m++) {
					String[] memberName = generateRandomName(random, FAMOUS_NAMES);
					if (firstMemberName == null)
						firstMemberName = memberName;
					CommitteeMember member = sub.addCommitteeMember(memberName[0], memberName[1], memberName[2]).save();
					
					if (random.nextInt(100) > 75) {
						DegreeLevelArray role = ROLETYPES_DEFINITIONS[random.nextInt(ROLETYPES_DEFINITIONS.length-1)];
						member.addRole(role.name);
						
						if (random.nextInt(100) > 75) {
							role = ROLETYPES_DEFINITIONS[random.nextInt(ROLETYPES_DEFINITIONS.length-1)];
							try {
								// We could pick the same role twice.
								member.addRole(role.name);
							} catch (IllegalArgumentException iae) {/* ignore */};
						}
					}
					member.save();
				}
				
				if (random.nextInt(100) > 5 && firstMemberName != null)
					sub.setCommitteeContactEmail(generateRandomEmail(random,firstMemberName));
							
				if (random.nextInt(100) > 5) {
					DegreeLevelArray degree = DEGREES_DEFINITIONS[random.nextInt(DEGREES_DEFINITIONS.length-1)];
					sub.setDegree(degree.name);
					sub.setDegreeLevel(degree.degreeLevel);
				}
				
				if (random.nextInt(100) > 5)
					sub.setDocumentTitle(generateRandomTitle(random));
				
				if (random.nextInt(100) > 5)
					sub.setDocumentAbstract(generateRandomAbstract(random));
				
				if (random.nextInt(100) > 5)
					sub.setDocumentKeywords(generateRandomKeywords(random));				
				
				if (random.nextInt(100) > 5) {
					List<ProquestSubject> subjects = proquestRepo.findAllSubjects();
					sub.addDocumentSubject(subjects.get(random.nextInt(subjects.size()-1)).getDescription());
					if (random.nextInt(100) > 5) {
						sub.addDocumentSubject(subjects.get(random.nextInt(subjects.size()-1)).getDescription());
						if (random.nextInt(100) > 5) {
							sub.addDocumentSubject(subjects.get(random.nextInt(subjects.size()-1)).getDescription());
						}
					}
				}
				
				if (random.nextInt(100) > 5) {
					sub.setDocumentLanguage(LANGUAGES_DEFINITIONS[random.nextInt(LANGUAGES_DEFINITIONS.length)]);
				}
				
				if (random.nextInt(100) > 5)
					sub.setDepartment(DEPARTMENTS_DEFINITIONS[random.nextInt(DEPARTMENTS_DEFINITIONS.length-1)]);
				
				if (random.nextInt(100) > 5)
					sub.setProgram(PROGRAMS_DEFINITIONS[random.nextInt(PROGRAMS_DEFINITIONS.length-1)]);
				
				if (random.nextInt(100) > 5)
					sub.setCollege(COLLEGES_DEFINITIONS[random.nextInt(COLLEGES_DEFINITIONS.length-1)]);
				
				if (random.nextInt(100) > 5)
					sub.setMajor(MAJORS_DEFINITIONS[random.nextInt(MAJORS_DEFINITIONS.length-1)]);
				
				if (random.nextInt(100) > 5)
					sub.setDocumentType(DOCTYPES_DEFINITIONS[random.nextInt(DOCTYPES_DEFINITIONS.length-1)].name);
				
				if (random.nextInt(100) > 5) {
					sub.setGraduationYear(random.nextInt(10)+2002);
					sub.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[random.nextInt(GRAD_MONTHS_DEFINITIONS.length-1)]);
				}
				
				if (random.nextInt(100) > 50)
					sub.setDefenseDate(generateRandomDate(random,2,2010));
				
				if (random.nextInt(100) > 5)
					sub.setUMIRelease(random.nextBoolean());
				
				if (random.nextInt(100) > 5)
					sub.setSubmissionDate(generateRandomDate(random,2,2010));
				
				if (random.nextInt(100) > 70)
					sub.setApprovalDate(generateRandomDate(random,2,2010));
				
				if (random.nextInt(100) > 50)
					sub.setCommitteeEmbargoApprovalDate(generateRandomDate(random,2,2010));
				
				if (random.nextInt(100) > 50)
					sub.setCommitteeApprovalDate(generateRandomDate(random,2,2010));
				
				if (random.nextInt(100) > 5)
					sub.setCommitteeEmailHash(generateCommitteEmailHash());
				
				if (random.nextInt(100) > 5) 
					sub.addAttachment(new File("test/SamplePrimaryDocument.pdf"),AttachmentType.PRIMARY);
				
				if (random.nextInt(100) > 20) {
					Date agreementDate = generateRandomDate(random,2,2010);
					String stampedLicense = stampLicense(settingRepo.getConfigValue(SUBMIT_LICENSE_TEXT), agreementDate);
					sub.addAttachment(stampedLicense.getBytes(), "LICENSE.txt", AttachmentType.LICENSE);
					sub.setLicenseAgreementDate(agreementDate);
				}
				
				if (random.nextInt(100) > 35) {
					Date agreementDate = generateRandomDate(random,2,2010);
					String stampedLicense = stampLicense(settingRepo.getConfigValue(PROQUEST_LICENSE_TEXT), agreementDate);
					sub.addAttachment(stampedLicense.getBytes(), "PROQUEST_LICENSE.txt", AttachmentType.LICENSE);
				}
				
				if (random.nextInt(100) > 75)
					sub.addAttachment(new File("test/SampleSupplementalDocument.doc"),AttachmentType.SUPPLEMENTAL);
				
				if (random.nextInt(100) > 75)
					sub.addAttachment(new File("test/SampleSupplementalDocument.xls"),AttachmentType.SUPPLEMENTAL);
	
				if (random.nextInt(100) > 50)
					sub.addAttachment(new File("test/SampleFeedbackDocument.png"),AttachmentType.FEEDBACK);
				
				context.turnOffAuthorization();
				if (random.nextInt(100) > 50)
					sub.setReviewerNotes(generateRandomTitle(random));
				context.restoreAuthorization();
			}
			
			sub.save();

			context.logout();
			
			if(i>0) {
				// Generate modifications to the 
				context.login(reviewer);
				int actionLogs = random.nextInt(30)+10;
				for (int l = 0; l < actionLogs; l++) {
					
					if (random.nextInt(100) > 30 ) {
						// Create randomly generated action.
						sub.logAction("Randomly generated action");
					} else {
						State state = sub.getState();					
						List<State> transitions = state.getTransitions(sub);
						if (transitions.size() == 0)
							transitions = stateManager.getAllStates();
					
						if (transitions.size() == 1) {
							
							sub.setState(transitions.get(0));
						} else {
							sub.setState(transitions.get(random.nextInt(transitions.size()-1)));
						}
					}
					sub.save();
				}
				context.logout();
			}

			
			
			
			if (i > 0 && i % 100 == 0) {
				// Do a database commit every 100 transactions.
				
				JPA.em().getTransaction().commit();
				JPA.em().clear();
				JPA.em().getTransaction().begin();
				
				// Reload persistant objects
				embargos = settingRepo.findAllEmbargoTypes();
				reviewer = personRepo.findPersonByEmail("jdimaggio@gmail.com");
				
				Logger.debug("Random submission generator: "+i+" submissions at "+ ((System.currentTimeMillis() - start)/i) +" milleseconds per submission (in progress)");
			}
		}
		Logger.debug("Random submission generator: "+howMany+" submissions at "+ ((System.currentTimeMillis() - start)/howMany) +" milleseconds per submission (finished)");
		
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	
	/**
	 * Generate a random security hash. The hash must be unique and not
	 * currently being used by a submission.
	 * 
	 * @return The new hash.
	 */
	public static String generateCommitteEmailHash() {

		String hash = null;

		do {		
			byte[] randomBytes = new byte[8];
			new Random().nextBytes(randomBytes);
			String proposed = Base64.encodeBase64URLSafeString(randomBytes);
			proposed = proposed.replaceAll("[^A-Za-z0-9]","");
						
			// Check if the hash already exists
			if (subRepo.findSubmissionByEmailHash(proposed) == null) {
				// We're done, otherwise keep looping.
				hash = proposed;
			}
		} while (hash == null);

		return hash;
	}
	
	/**
	 * Stamp the license with who is accepting the license and the current date.
	 * This way this information will be stored directly with the license text.
	 * 
	 * @param licenseText
	 *            The license text.
	 * @param agreementDate
	 *            The exact date of agreement.
	 * @return The stamped license text
	 */
	public static String stampLicense(String licenseText, Date agreementDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm a");
		Person submitter = context.getPerson();

		licenseText += "\n\n--------------------------------------------------------------------------\n";
		licenseText += "The license above was accepted by "+submitter.getFormattedName(NameFormat.FIRST_LAST)+" on "+formatter.format(agreementDate)+"\n";

		return licenseText;

	}
	
	/**
	 * Generate a random date.
	 * 
	 * @param random The random number generator.
	 * @param spread The range of possible random year.
	 * @param start The start year.
	 * @return A random date.
	 */
	public static Date generateRandomDate(Random random, int spread, int start) {

		int year = random.nextInt(spread) + start;
		int month = random.nextInt(11);
		int day = random.nextInt(28);
		
		return new Date(year-1900,month,day);
	}
	
	/**
	 * Generate a random name from the provided list. An array of 3 strings will
	 * be returned:
	 * 
	 * [0] The First Name
	 * 
	 * [1] The Last Name
	 * 
	 * [3] The Middle Name (may be null)
	 * 
	 * @param random
	 *            The random number generator.
	 * @param listOfNames
	 *            A list of names.
	 * @return An array of 3 strings for first, last and middle names.
	 */
	public static String[] generateRandomName(Random random, String[] listOfNames) {
		
		String fullName = listOfNames[random.nextInt(listOfNames.length-1)];
		
		String[] parts = fullName.split(" ");
		
		String[] result = new String[3];
		try {
		
		if (parts.length > 2) {
			result[0] = parts[0];// first
			result[1] = parts[2];// last
			result[2] = parts[1];// middle
		} else {
			result[0] = parts[0];
			result[1] = parts[1];
			result[2] = null;
		}
		} catch (Throwable t) {
			return null;
		}
		
		return result;
	}
	
	/**
	 * Generate a random title.
	 * 
	 * Titles are between 7 and 13 words long, and all words are capitalized
	 * with no punctuation. All words are basic English.
	 * 
	 * @param random
	 *            A random number generator.
	 * @return A random title.
	 */
	public static String generateRandomTitle(Random random) {
		
		int words = random.nextInt(10)+3;
		
		String title = "";
		for(int i=0; i<words; i++) {
			
			String word = BASIC_WORDS[random.nextInt(BASIC_WORDS.length-1)];
			
			if (i > 0)
				title += " ";
			title += word.substring(0,1).toUpperCase() + word.substring(1) ; 
		}
		return title;
	}

	/**
	 * Generate a random set of keywords.
	 * 
	 * Keywords are basic English works separated by semicolons, each word is
	 * capitalized.
	 * 
	 * @param random
	 *            A random number generator.
	 * @return A random set of keywords
	 */
	public static String generateRandomKeywords(Random random) {

		int words = random.nextInt(5);
		
		String keywords = "";
		for(int i=0; i<words; i++) {
			
			String word = BASIC_WORDS[random.nextInt(BASIC_WORDS.length-1)];
			
			if (i > 0)
				keywords += "; ";
			keywords += word.substring(0,1).toUpperCase() + word.substring(1) ; 
		}
		return keywords;
	}
	
	/**
	 * Generate a random abstract.
	 * 
	 * Abstracts are between 35 and 65 words long. They will contain periods and
	 * some capitalized words.
	 * 
	 * @param random
	 *            A random number generator
	 * @return A random abstract
	 */
	public static String generateRandomAbstract(Random random) {

		int words = random.nextInt(100)+15;
		
		String text = "";
		for(int i=0; i<words; i++) {
			
			String word = BASIC_WORDS[random.nextInt(BASIC_WORDS.length-1)];

			if (i == 0) {
				text += word.substring(0,1).toUpperCase() + word.substring(1);
			} else {
				
				if (random.nextInt(100) > 80) {
					text += ". ";
					text += word.substring(0,1).toUpperCase() + word.substring(1);
				} else {
					text += " ";
					text += word;
				}
			}
		}
		return text;
	}
	

	/**
	 * Generate a random email address based upon the name provided.
	 * 
	 * @param random The random number generator.
	 * @param name The name.
	 * @return An email address.
	 */
	public static String generateRandomEmail(Random random, String[] name) {

		String firstName = name[0];
		String lastName = name[1];
		
		String domain = EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length-1)];
		
		return firstName+"."+lastName+"@"+domain;
	}
	
	
	private static class ConfigurationArray {
		String name;
		String value;
		
		ConfigurationArray(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private static class DegreeLevelArray {
		String name;
		DegreeLevel degreeLevel;
		
		DegreeLevelArray(String name, DegreeLevel degreeLevel) {
			this.name = name;
			this.degreeLevel = degreeLevel;
		}
	}
	
	private static class PersonsArray {
		String netId;
		String email;
		String firstName;
		String lastName;
		String password;
		RoleType role;
		
		PersonsArray(String netId, String email, String firstName, String lastName, String password, RoleType role) {
			this.netId = netId;
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
			this.password = password;
			this.role = role;
		}
	}
	
	private static class EmbargoArray{
		
		String name;
		String description;
		Integer duration;
		boolean active;
		
		EmbargoArray(String name, String description, Integer duration, boolean active) {
			this.name = name;
			this.description = description;
			this.duration = duration;
			this.active = active;
		}
		
		
	}
	
	private static class EmailTemplateArray {
		
		String name;
		String subject;
		String message;
		
		EmailTemplateArray(String name, String subject, String message) {
			this.name= name;
			this.subject=subject;
			this.message=message;
		}
	}
	
	private static class DepositLocationArray {
		
		String name;
		String repository;
		String collection;
		String username;
		String password;
		String onBehalfOf;
		String packager;
		String depositor;
		
		DepositLocationArray(String name, String repository,
				String collection, String username, String password,
				String onBehalfOf, String packager, String depositor) {
			this.name = name;
			this.repository = repository;
			this.collection = collection;
			this.username = username;
			this.password = password;
			this.onBehalfOf = onBehalfOf;
			this.packager = packager;
			this.depositor = depositor;
		}
	}
	
	/** List of email domains, everything past the @ sign **/
	public static final String[] EMAIL_DOMAINS = {
		"gmail.com",
		"yahoo.com",
		"hotmail.com",
		"aol.com",
		"tamu.edu",
		"library.tamu.edu",
		"ut.edu",
		"tdl.org",
		"uh.edu",
		"mit.edu",
		"illinois.edu"
	};
	
	/** List of names of famous people **/
	public static final String[] FAMOUS_NAMES = { "Elvis Presley",
			"Abraham Lincoln", "Benjamin Franklin", "Leonardo Da Vinci",
			"Walt Disney", "John F. Kennedy", "George Washington",
			"Christopher Columbus", "Bill Clinton", "Princess Diana",
			"Paul McCartney", "William Shakespeare", "Albert Einstein",
			"Neil Armstrong", "Mother Teresa", "Thomas Edison",
			"Charles Dickens", "Ludwig van Beethoven", "John Lennon",
			"Mohammad Ali", "Bill Gates", "George W. Bush", "Thomas Jefferson",
			"Alfred Hitchcock", "Spider Man", "Eddie Murphy", "Marilyn Monroe",
			"Margaret Thatcher", "Martin Luther King", "Ronald Reagan",
			"Tom Cruise", "Mark Twain", "Alexander Graham Bell",
			"Edgar Allen Poe", "Pablo Piccaso", "Helen Keller", "Ray Charles",
			"Hillary Clinton", "Isaac Newton", "Vladimir Putin",
			"Clint Eastwood", "Vincent Van Gogh", "Oprah Winfrey",
			"Jack Nicholson", "Mick Jagger", "Tom Hanks", "Lucille Ball",
			"Bill Cosby", "Neil Diamond", "Michael Jackson",
			"Elizabeth Taylor", "Michael Jordan", "Louis Pasteur",
			"Britney Spears", "Robin Williams", "Dwight D. Eisenhower",
			"Darth Vader", "Andy Griffith", "Keith Richards", "Sean Connery",
			"Charlie Brown", "Susan B. Anthony", "Bob Dylan", "Hank Aaron",
			"John Travolta", "Sigmund Freud", "Steve Martin", "James Dean",
			"Steve Irwin", "Jacqueline Kennedy Onasis", "Harrison Ford",
			"Ernest Hemingway", "James Taylor", "Whoopi Goldberg",
			"Denzel Washington", "Yogi Berra", "Justin Timberlake",
			"Henry A. Kissinger", "Bob Hope", "Jim Carrey", "Dan Rather",
			"Michael J. Fox", "Paul Newman", "Kanye West", "Michael Landon",
			"Stephen Hawking", "Lewis Carrol", "Fred Astaire", "Jesse Jackson",
			"Tyra Banks", "Chevy Chase", "John Candy", "Magic Johnson",
			"Walter Cronkite", "Newt Gingrich", "Meryl Streep", "Roy Rogers",
			"Carl Sagan", "Frank Lloyd Wright", "Julia Roberts", "Jane Austen",
			"David Letterman", "Peyton Manning", "Terry Bradshaw",
			"Billy Crystal", "Pamela Anderson", "Tom Brokaw", "W.C. Fields",
			"Willey Mays", "Howard Stern", "Bob Newhart", "Peter Jennings",
			"Andy Rooney", "Ross Perot", "Paul Harvey", "Michele Pfeiffer",
			"Carol Burnett", "Bill Hicks", "Mae West", "Ashley Simpson",
			"Chuck Yeager", "Miles Davis", "Jessica Simpson", "Julia Child",
			"Weird Al Yankovic", "Carrie Fisher", "Elizabeth Dole",
			"Jimmy Conners", "Danny Glover", "Nathaniel Hawthorne",
			"Benny Goodman", "Colin L. Powell", "Norman Rockwell",
			"Arthur Ashe", "Sigourney Weaver", "Anna Nicole Smith",
			"Shirley MacLaine", "Marilyn Vos Savant", "Norman Schwarzkopf",
			"Dan Aykroyd", "George Carlin", "Tommy Lee", "Gloria Steinem",
			"Henri Mancini", "Alicia Silverstone", "Phil Donahue",
			"Charles Everett Koop" };
	
	
	/** List of actor names */
	public static final String[] ACTOR_NAMES = { "Fred Astaire",
			"Lauren Bacall", "Brigitte Bardot", "John Belushi",
			"Ingmar Bergman", "Ingrid Bergman", "Humphrey Bogart",
			"Marlon Brando", "James Cagney", "Gary Cooper", "Bette Davis",
			"Doris Day", "Olivia de Havilland", "James Dean",
			"Georges Delerue", "Marlene Dietrich", "Kirk Douglas",
			"Henry Fonda", "Joan Fontaine", "Clark Gable", "Judy Garland",
			"John Gielgud", "Jerry Goldsmith", "Cary Grant", "Alec Guinness",
			"Rita Hayworth", "Audrey Hepburn", "Katharine Hepburn",
			"Charlton Heston", "Alfred Hitchcock", "William Holden",
			"James Horner", "Buster Keaton", "Gene Kelly", "Grace Kelly",
			"Stanley Kubrick", "Akira Kurosawa", "Alan Ladd", "Veronica Lake",
			"Burt Lancaster", "Bruce Lee", "Vivien Leigh", "Sophia Loren",
			"Peter Lorre", "Groucho Marx", "James Mason",
			"Marcello Mastroianni", "Robert Mitchum", "Marilyn Monroe",
			"Alfred Newman", "Paul Newman", "David Niven", "Gregory Peck",
			"Tyrone Power", "Anthony Quinn", "Nino Rota", "Jane Russell",
			"Randolph Scott", "Max Steiner", "James Stewart",
			"Elizabeth Taylor", "Shirley Temple", "Gene Tierney",
			"Spencer Tracy", "Franz Waxman", "John Wayne", "Orson Welles",
			"Natalie Wood", "Victor Young", "Alan Miller", "Li Gong",
			"Henner Hofmann", "Yelena Koreneva", "Aleksei Korenev",
			"John Cleese", "Brad Pitt", "Woody Allen", "Gillian Anderson",
			"Pamela Anderson", "Jennifer Aniston", "Rowan Atkinson",
			"Dan Aykroyd", "Kevin Bacon", "Fairuza Balk", "Antonio Banderas",
			"Adrienne Barbeau", "Drew Barrymore", "Kim Basinger", "Luc Besson",
			"Kenneth Branagh", "Matthew Broderick", "Pierce Brosnan",
			"Sandra Bullock", "Steve Buscemi", "Nicolas Cage", "James Cameron",
			"Neve Campbell", "John Carpenter", "Jim Carrey", "Phoebe Cates",
			"Charles Chaplin", "George Clooney", "Jennifer Connelly",
			"Sean Connery", "Kevin Costner", "Wes Craven", "Russell Crowe",
			"Jamie Lee Curtis", "John Cusack", "Claire Danes", "Geena Davis",
			"Robert De Niro", "John Denver", "Johnny Depp", "Bo Derek",
			"Leonardo DiCaprio", "Michael Douglas", "David Duchovny",
			"Clint Eastwood", "Erika Eleniak", "Cary Elwes", "Sherilyn Fenn",
			"Ralph Fiennes", "Colin Firth", "Harrison Ford", "Morgan Freeman",
			"Richard Gere", "Gina Gershon", "Mel Gibson", "Whoopi Goldberg",
			"Jeff Goldblum", "Linda Hamilton", "Tom Hanks", "Ethan Hawke",
			"Salma Hayek", "Anne Heche", "Dustin Hoffman", "Anthony Hopkins",
			"Ron Howard", "Helen Hunt", "Elizabeth Hurley", "Milla Jovovich",
			"Ashley Judd", "Harvey Keitel", "Nicole Kidman", "Val Kilmer",
			"Stephen King", "Nastassja Kinski", "Kevin Kline", "Diane Lane",
			"David Lean", "Heather Locklear", "Jennifer Lopez", "Traci Lords",
			"George Lucas", "Dolph Lundgren", "David Lynch", "Steve Martin",
			"Matthew McConaughey", "Ewan McGregor", "Alyssa Milano",
			"Demi Moore", "Julianne Moore", "Bill Murray", "Mike Myers",
			"Jack Nicholson", "Gary Oldman", "Bill Paxton",
			"Michelle Pfeiffer", "Ryan Phillippe", "River Phoenix",
			"Natalie Portman", "Parker Posey", "Keanu Reeves",
			"Christina Ricci", "Molly Ringwald", "Julia Roberts",
			"Mimi Rogers", "Meg Ryan", "Winona Ryder", "Mia Sara",
			"Susan Sarandon", "Arnold Schwarzenegger", "Martin Scorsese",
			"Kristin Scott Thomas", "Joan Severance", "Charlie Sheen",
			"Brooke Shields", "Elisabeth Shue", "Alicia Silverstone",
			"Christian Slater", "Will Smith", "Mira Sorvino", "Kevin Spacey",
			"Sylvester Stallone", "Oliver Stone", "Sharon Stone",
			"Quentin Tarantino", "Charlize Theron", "Uma Thurman",
			"Jennifer Tilly", "John Travolta", "Shannon Tweed", "Skeet Ulrich",
			"Mark Wahlberg", "Sigourney Weaver", "Robin Williams",
			"Bruce Willis", "John Woo", "Timothy Dowling", "Robert Ellis",
			"Robert Ellis", "Isabelle Adjani", "Ben Affleck", "Jenny Agutter",
			"Alan Alda", "Stephanie Zimbalist", "Joan Allen", "Karen Allen",
			"Nancy Allen", "Kirstie Alley", "Robert Altman", "Ursula Andress",
			"Julie Andrews", "Gabrielle Anwar", "Anne Archer", "Fanny Ardant",
			"Alan Arkin", "David Arquette", "Rosanna Arquette", "Sean Astin",
			"Richard Attenborough", "Pernilla August", "Catherine Bach",
			"Scott Baio", "Scott Bairstow", "Brenda Bakke", "Adam Baldwin",
			"Alec Baldwin", "Stephen Baldwin", "William Baldwin",
			"Christian Bale", "John Barry", "Angela Bassett", "Michelle Bauer",
			"Sean Bean", "Amanda Bearse", "Kate Beckinsale", "Robert Beltran",
			"Tom Berenger", "Candice Bergen", "Juliette Binoche",
			"Thora Birch", "Jacqueline Bisset", "Honor Blackman",
			"Linda Blair", "Mel Blanc", "Brian Blessed",
			"Helena Bonham Carter", "Ernest Borgnine", "Bruce Boxleitner",
			"Annie Rosar", "Amy Brenneman", "Jeff Bridges", "Charles Bronson",
			"Louise Brooks", "Mel Brooks", "Clancy Brown", "Tim Burton",
			"Gabriel Byrne", "Michael Caine", "Barbara Carrera",
			"David Caruso", "Kim Cattrall", "Lacey Chabert",
			"Richard Chamberlain", "Stockard Channing", "Chevy Chase",
			"Don Cheadle", "Glenn Close", "James Coburn", "Rachael Leigh Cook",
			"Francis Ford Coppola", "Cindy Crawford", "Michael Crichton",
			"James Cromwell", "David Cronenberg", "Denise Crosby",
			"Billy Crystal", "Macaulay Culkin", "Tim Curry", "Tony Curtis",
			"Willem Dafoe", "Matt Damon", "Anthony Daniels", "Sybil Danning",
			"Lolita Davidovich", "Rebecca De Mornay", "Brian De Palma",
			"Danny DeVito", "Loren Dean", "Sandra Dee", "Julie Delpy",
			"Catherine Deneuve", "Laura Dern", "Walt Disney", "Ami Dolenz",
			"Amanda Donohoe", "Michael Dorn", "Brad Dourif", "Fran Drescher",
			"Richard Dreyfuss", "Minnie Driver", "Robert Duvall",
			"Anthony Edwards", "Atom Egoyan", "Jennifer Ehle", "Danny Elfman",
			"Sam Elliott", "Roland Emmerich", "Robert Englund",
			"Joe Eszterhas", "Rupert Everett", "Morgan Fairchild",
			"Peter Falk", "Chris Farley", "Terry Farrell", "Farrah Fawcett",
			"Corey Feldman", "Sally Field", "Linda Fiorentino",
			"Laurence Fishburne", "Carrie Fisher", "Bridget Fonda",
			"Jane Fonda", "Michelle Forbes", "John Ford", "Jonathan Frakes",
			"Stephen Fry", "Edward Furlong", "Andy Garcia", "Janeane Garofalo",
			"Teri Garr", "Jami Gertz", "Terry Gilliam", "Crispin Glover",
			"Danny Glover", "Valeria Golino", "John Goodman", "Serena Grandi",
			"Hugh Grant", "Peter Greenaway", "Jennifer Grey", "Pam Grier",
			"Steve Guttenberg", "Taylor Hackford", "Gene Hackman",
			"Corey Haim", "Mark Hamill", "Daryl Hannah", "Curtis Hanson",
			"Woody Harrelson", "Ed Harris", "Nina Hartley", "Noah Hathaway",
			"Rutger Hauer", "Goldie Hawn", "Glenne Headly", "Dan Hedaya",
			"Mariel Hemingway", "Marilu Henner", "Lance Henriksen",
			"Philip Seymour Hoffman", "Gaby Hoffmann", "Lauren Holly",
			"Ian Holm", "Dennis Hopper", "John Hughes", "Holly Hunter",
			"John Hurt", "William Hurt", "Jeremy Irons", "Michael Ironside",
			"Kate Jackson", "Famke Janssen", "Jim Jarmusch", "Ron Jeremy",
			"Don Johnson", "Jeffrey Jones", "Raul Julia", "Boris Karloff",
			"Diane Keaton", "Michael Keaton", "Patsy Kensit", "Sally Kirkland",
			"Mia Kirshner", "Tawny Kitaen", "Elias Koteas", "Alice Krige",
			"Sylvia Kristel", "Christopher Lambert", "John Landis",
			"Fritz Lang", "Heather Langenkamp", "Ang Lee", "Brandon Lee",
			"Spike Lee", "John Leguizamo", "Jennifer Jason Leigh",
			"Jack Lemmon", "Robert Sean Leonard", "Juliette Lewis",
			"Jennifer Lien", "Matthew Lillard", "Richard Linklater",
			"Ray Liotta", "Christopher Lloyd", "Emily Lloyd", "Amy Locane",
			"Nia Long", "Rob Lowe", "Carey Lowell", "Andie MacDowell",
			"Shirley MacLaine", "Elle Macpherson", "Michael Madsen",
			"Virginia Madsen", "Lee Majors", "Terrence Malick",
			"John Malkovich", "Michael Mann", "Sophie Marceau",
			"Vanessa Marcil", "Julianna Margulies", "Mary Stuart Masterson",
			"Heather Matarazzo", "Samantha Mathis", "Walter Matthau",
			"Mathilda May", "Andrew McCarthy", "Frances McDormand",
			"Malcolm McDowell", "Gates McFadden", "Kelly McGillis",
			"Rose McGowan", "Robert Duncan McNeill", "Steve McQueen",
			"Colm Meaney", "Russ Meyer", "Bette Midler", "Penelope Ann Miller",
			"Sal Mineo", "Carmen Miranda", "Helen Mirren", "Matthew Modine",
			"Alfred Molina", "Elizabeth Montgomery", "Kate Mulgrew",
			"Dermot Mulroney", "Eddie Murphy", "Liam Neeson", "Sam Neill",
			"Judd Nelson", "Brigitte Nielsen", "Leslie Nielsen", "Nick Nolte",
			"Peter North", "Jeremy Northam", "Lena Olin", "Julia Ormond",
			"George Orwell", "Frank Oz", "Alan Parker", "Sarah Jessica Parker",
			"Dolly Parton", "Jason Patric", "Alexandra Paul", "Sean Penn",
			"George Peppard", "Anthony Perkins", "Luke Perry", "Joe Pesci",
			"Wolfgang Petersen", "Amanda Peterson", "Robert Picardo",
			"Jada Pinkett Smith", "Donald Pleasence", "Martha Plimpton",
			"Edgar Allan Poe", "Roman Polanski", "Pete Postlethwaite",
			"Kelly Preston", "Jason Priestley", "Victoria Principal",
			"Jonathan Pryce", "Bill Pullman", "Dennis Quaid", "Sam Raimi",
			"Harold Ramis", "Robert Redford", "Vanessa Redgrave",
			"Brad Renfro", "Jean Reno", "Paul Reubens", "Burt Reynolds",
			"Giovanni Ribisi", "Ariana Richards", "Denise Richards",
			"Joely Richardson", "Alan Rickman", "John Ritter", "Eric Roberts",
			"Tanya Roberts", "Isabella Rossellini", "Mickey Rourke",
			"Kurt Russell", "Theresa Russell", "Rene Russo",
			"Laura San Giacomo", "Fred Savage", "John Sayles", "Greta Scacchi",
			"Johnathon Schaech", "Liev Schreiber", "Ridley Scott",
			"Jerry Seinfeld", "Tom Selleck", "Peter Sellers", "Yahoo Serious",
			"William Shakespeare", "Tupac Shakur", "William Shatner",
			"Martin Sheen", "Gary Sinise", "Marina Sirtis", "Tom Skerritt",
			"Helen Slater", "Anna Nicole Smith", "Jaclyn Smith",
			"Alan Smithee", "Wesley Snipes", "Talisa Soto", "Sissy Spacek",
			"James Spader", "Brent Spiner", "Terence Stamp", "Eric Stoltz",
			"Madeleine Stowe", "David Strathairn", "Meryl Streep",
			"Tami Stronach", "Donald Sutherland", "Kiefer Sutherland",
			"Dominique Swain", "Patrick Swayze", "Lili Taylor",
			"David Thewlis", "Emma Thompson", "Lea Thompson",
			"Billy Bob Thornton", "Meg Tilly", "Marisa Tomei", "Tamlyn Tomita",
			"Jeanne Tripplehorn", "Chris Tucker", "Robin Tunney",
			"Kathleen Turner", "Casper Van Dien", "Vince Vaughn",
			"Paul Verhoeven", "Gore Vidal", "Nana Visitor", "Jon Voight",
			"Christopher Walken", "Sela Ward", "Lesley Ann Warren",
			"John Waters", "Teri Weigel", "Peter Weller", "Wim Wenders",
			"Joanne Whalley", "Wil Wheaton", "Billy Wilder", "Gene Wilder",
			"Debra Winger", "Kate Winslet", "Reese Witherspoon", "BD Wong",
			"Elijah Wood", "Robin Wright", "Michelle Yeoh", "Sean Young",
			"Billy Zane", "David Raksin", "Rick Baker", "Steve Cohen",
			"Joel Oliansky", "Willie Aames", "Caroline Aaron", "Paula Abdul",
			"Ian Abercrombie", "Jim Abrahams", "Victoria Abril",
			"Joss Ackland", "Deborah Adair", "Brooke Adams",
			"Joey Lauren Adams", "Maud Adams", "Percy Adlon", "Mario Adorf",
			"John Agar", "Brian Aherne", "Danny Aiello", "Eddie Albert",
			"Brian Aldiss", "Robert Aldrich", "Jane Alexander", "Muhammad Ali",
			"Irwin Allen", "Tim Allen", "June Allyson",
			"Maria Conchita Alonso", "Carol Alt", "Trini Alvarado",
			"Don Ameche", "Leon Ames", "Jon Amiel", "Suzy Amis",
			"Judith Anderson", "Juliet Anderson", "Kevin Anderson",
			"Lindsay Anderson", "Loni Anderson", "Melissa Sue Anderson",
			"Melody Anderson", "Richard Dean Anderson", "Bibi Andersson",
			"Anthony Andrews", "Dana Andrews", "Pier Angeli",
			"Theodoros Angelopoulos", "Francesca Annis", "David Anspaugh",
			"Lysette Anthony", "Susan Anton", "Laura Antonelli",
			"Michelangelo Antonioni", "Christina Applegate", "Michael Apted",
			"Gregg Araki", "Alfonso Arau", "Denys Arcand", "Eve Arden",
			"Asia Argento", "Dario Argento", "Alison Armitage",
			"George Armitage", "Bess Armstrong", "Gillian Armstrong",
			"James Arness", "Jack Arnold", "Tom Arnold", "Alexis Arquette",
			"Lewis Arquette", "Jean Arthur", "Dana Ashbrook", "Hal Ashby",
			"Linden Ashby", "Armand Assante", "Olivier Assayas", "Mary Astor",
			"Christopher Atkins", "Claudine Auger", "Bille August",
			"Jane Austen", "Paul Auster", "Gene Autry", "Frankie Avalon",
			"Roger Avary", "Tex Avery", "Mili Avital", "Jon Avnet",
			"Lew Ayres", "Shabana Azmi", "Burt Bacharach", "Amitabh Bachchan",
			"Jim Backus", "Angelo Badalamenti", "John Badham", "Mary Badham",
			"Jane Badler", "Maxine Bahns", "Barbara Bain", "Oksana Baiul",
			"Diane Baker", "Joe Don Baker", "Kathy Baker", "Ralph Bakshi",
			"Scott Bakula", "Bob Balaban", "Daniel Baldwin", "Lucille Ball",
			"Michael Ballhaus", "Martin Balsam", "Anne Bancroft",
			"Tamasaburo Bando", "Tallulah Bankhead", "Ian Bannen",
			"Theda Bara", "Olivia Barash", "Clive Barker", "Lex Barker",
			"Bruno Barreto", "Majel Barrett", "Ethel Barrymore",
			"John Drew Barrymore", "John Barrymore", "Paul Bartel",
			"Freddie Bartholomew", "Robin Bartlett", "Billy Barty",
			"Mikhail Baryshnikov", "Richard Basehart", "Saul Bass",
			"Jason Bateman", "Justine Bateman", "Kathy Bates",
			"Randall Batinkoff", "Patrick Bauchau", "Belinda Bauer",
			"Steven Bauer", "Noah Baumbach", "Lamberto Bava", "Mario Bava",
			"Meredith Baxter", "Michael Bay", "Nathalie Baye",
			"Stephanie Beacham", "Jennifer Beals", "Ned Beatty",
			"Warren Beatty", "Harold Becker", "Meret Becker", "Wallace Beery",
			"Jason Beghe", "Barbara Bel Geddes", "Harry Belafonte",
			"Ralph Bellamy", "Kathleen Beller", "Pamela Bellwood",
			"James Belushi", "Brian Benben", "William Bendix",
			"Roberto Benigni", "Annette Bening", "Richard Benjamin",
			"David Bennent", "Joan Bennett", "Nigel Bennett", "Jack Benny",
			"Robby Benson", "Robert Benton", "Bruce Beresford", "Peter Berg",
			"Polly Bergen", "Helmut Berger", "Patrick Bergin",
			"Andrew Bergman", "Sandahl Bergman", "Busby Berkeley",
			"Elizabeth Berkley", "Steven Berkoff", "Milton Berle",
			"Irving Berlin", "Sandra Bernhard", "Elmer Bernstein",
			"Elizabeth Berridge", "Halle Berry", "Valerie Bertinelli",
			"Bernardo Bertolucci", "Bibi Besch", "Martine Beswick",
			"Richard Beymer", "Daniela Bianchi", "Bigas Luna",
			"Kathryn Bigelow", "Theodore Bikel", "Traci Bingham",
			"Antonia Bird", "Jane Birkin", "Whit Bissell", "Karen Black",
			"Shane Black", "Brenda Blethyn", "Joan Blondell", "Hart Bochner",
			"Peter Bogdanovich", "Jon Bon Jovi", "Ward Bond", "Lisa Bonet",
			"Jan de Bont", "John Boorman", "Barry Bostwick", "Timothy Bottoms",
			"Carole Bouquet", "Stephen Boyd", "Charles Boyer", "Danny Boyle",
			"Lorraine Bracco", "Eric Braeden", "Sonia Braga",
			"Jonathan Brandis", "Nicoletta Braschi", "Tinto Brass",
			"Benjamin Bratt", "Walter Brennan", "Robert Bresson",
			"Martin Brest", "Beau Bridges", "Lloyd Bridges", "Jim Broadbent",
			"James Brolin", "Josh Brolin", "Albert Brooks", "Avery Brooks",
			"Bryan Brown", "Jim Brown", "Jerry Bruckheimer", "Betty Buckley",
			"Billie Burke", "Carol Burnett", "Raymond Burr", "Ellen Burstyn",
			"LeVar Burton", "Gary Busey", "Jake Busey", "Tom Byron" };
	
	/**
	 * List of 850 basic English words
	 */
	public static final String[] BASIC_WORDS = { "a", "able", "about",
			"account", "acid", "across", "act", "addition", "adjustment",
			"advertisement", "after", "again", "against", "agreement", "air",
			"all", "almost", "among", "amount", "amusement", "and", "angle",
			"angry", "animal", "answer", "ant", "any", "apparatus", "apple",
			"approval", "arch", "argument", "arm", "army", "art", "as", "at",
			"attack", "attempt", "attention", "attraction", "authority",
			"automatic", "awake", "baby", "back", "bad", "bag", "balance",
			"ball", "band", "base", "basin", "basket", "bath", "be",
			"beautiful", "because", "bed", "bee", "before", "behaviour",
			"belief", "bell", "bent", "berry", "between", "bird", "birth",
			"bit", "bite", "bitter", "black", "blade", "blood", "blow", "blue",
			"board", "boat", "body", "boiling", "bone", "book", "boot",
			"bottle", "box", "boy", "brain", "brake", "branch", "brass",
			"bread", "breath", "brick", "bridge", "bright", "broken",
			"brother", "brown", "brush", "bucket", "building", "bulb", "burn",
			"burst", "business", "but", "butter", "button", "by", "cake",
			"camera", "canvas", "card", "care", "carriage", "cart", "cat",
			"cause", "certain", "chain", "chalk", "chance", "change", "cheap",
			"cheese", "chemical", "chest", "chief", "chin", "church", "circle",
			"clean", "clear", "clock", "cloth", "cloud", "coal", "coat",
			"cold", "collar", "colour", "comb", "come", "comfort", "committee",
			"common", "company", "comparison", "competition", "complete",
			"complex", "condition", "connection", "conscious", "control",
			"cook", "copper", "copy", "cord", "cork", "cotton", "cough",
			"country", "cover", "cow", "crack", "credit", "crime", "cruel",
			"crush", "cry", "cup", "cup", "current", "curtain", "curve",
			"cushion", "damage", "danger", "dark", "daughter", "day", "dead",
			"dear", "death", "debt", "decision", "deep", "degree", "delicate",
			"dependent", "design", "desire", "destruction", "detail",
			"development", "different", "digestion", "direction", "dirty",
			"discovery", "discussion", "disease", "disgust", "distance",
			"distribution", "division", "do", "dog", "door", "doubt", "down",
			"drain", "drawer", "dress", "drink", "driving", "drop", "dry",
			"dust", "ear", "early", "earth", "east", "edge", "education",
			"effect", "egg", "elastic", "electric", "end", "engine", "enough",
			"equal", "error", "even", "event", "ever", "every", "example",
			"exchange", "existence", "expansion", "experience", "expert",
			"eye", "face", "fact", "fall", "false", "family", "far", "farm",
			"fat", "father", "fear", "feather", "feeble", "feeling", "female",
			"fertile", "fiction", "field", "fight", "finger", "fire", "first",
			"fish", "fixed", "flag", "flame", "flat", "flight", "floor",
			"flower", "fly", "fold", "food", "foolish", "foot", "for", "force",
			"fork", "form", "forward", "fowl", "frame", "free", "frequent",
			"friend", "from", "front", "fruit", "full", "future", "garden",
			"general", "get", "girl", "give", "glass", "glove", "go", "goat",
			"gold", "good", "government", "grain", "grass", "great", "green",
			"grey", "grip", "group", "growth", "guide", "gun", "hair",
			"hammer", "hand", "hanging", "happy", "harbour", "hard", "harmony",
			"hat", "hate", "have", "he", "head", "healthy", "hear", "hearing",
			"heart", "heat", "help", "high", "history", "hole", "hollow",
			"hook", "hope", "horn", "horse", "hospital", "hour", "house",
			"how", "humour", "I", "ice", "idea", "if", "ill", "important",
			"impulse", "in", "increase", "industry", "ink", "insect",
			"instrument", "insurance", "interest", "invention", "iron",
			"island", "jelly", "jewel", "join", "journey", "judge", "jump",
			"keep", "kettle", "key", "kick", "kind", "kiss", "knee", "knife",
			"knot", "knowledge", "land", "language", "last", "late", "laugh",
			"law", "lead", "leaf", "learning", "leather", "left", "leg", "let",
			"letter", "level", "library", "lift", "light", "like", "limit",
			"line", "linen", "lip", "liquid", "list", "little", "living",
			"lock", "long", "look", "loose", "loss", "loud", "love", "low",
			"machine", "make", "male", "man", "manager", "map", "mark",
			"market", "married", "mass", "match", "material", "may", "meal",
			"measure", "meat", "medical", "meeting", "memory", "metal",
			"middle", "military", "milk", "mind", "mine", "minute", "mist",
			"mixed", "money", "monkey", "month", "moon", "morning", "mother",
			"motion", "mountain", "mouth", "move", "much", "muscle", "music",
			"nail", "name", "narrow", "nation", "natural", "near", "necessary",
			"neck", "need", "needle", "nerve", "net", "new", "news", "night",
			"no", "noise", "normal", "north", "nose", "not", "note", "now",
			"number", "nut", "observation", "of", "off", "offer", "office",
			"oil", "old", "on", "only", "open", "operation", "opinion",
			"opposite", "or", "orange", "order", "organization", "ornament",
			"other", "out", "oven", "over", "owner", "page", "pain", "paint",
			"paper", "parallel", "parcel", "part", "past", "paste", "payment",
			"peace", "pen", "pencil", "person", "physical", "picture", "pig",
			"pin", "pipe", "place", "plane", "plant", "plate", "play",
			"please", "pleasure", "plough", "pocket", "point", "poison",
			"polish", "political", "poor", "porter", "position", "possible",
			"pot", "potato", "powder", "power", "present", "price", "print",
			"prison", "private", "probable", "process", "produce", "profit",
			"property", "prose", "protest", "public", "pull", "pump",
			"punishment", "purpose", "push", "put", "quality", "question",
			"quick", "quiet", "quite", "rail", "rain", "range", "rat", "rate",
			"ray", "reaction", "reading", "ready", "reason", "receipt",
			"record", "red", "regret", "regular", "relation", "religion",
			"representative", "request", "respect", "responsible", "rest",
			"reward", "rhythm", "rice", "right", "ring", "river", "road",
			"rod", "roll", "roof", "room", "root", "rough", "round", "rub",
			"rule", "run", "sad", "safe", "sail", "salt", "same", "sand",
			"say", "scale", "school", "science", "scissors", "screw", "sea",
			"seat", "second", "secret", "secretary", "see", "seed", "seem",
			"selection", "self", "send", "sense", "separate", "serious",
			"servant", "shade", "shake", "shame", "sharp", "sheep",
			"shelf", "ship", "shirt", "shock", "shoe", "short", "shut", "side",
			"sign", "silk", "silver", "simple", "sister", "size", "skin",
			"skirt", "sky", "sleep", "slip", "slope", "slow", "small", "smash",
			"smell", "smile", "smoke", "smooth", "snake", "sneeze", "snow",
			"so", "soap", "society", "sock", "soft", "solid", "some", "son",
			"song", "sort", "sound", "soup", "south", "space", "spade",
			"special", "sponge", "spoon", "spring", "square", "stage", "stamp",
			"star", "start", "statement", "station", "steam", "steel", "stem",
			"step", "stick", "sticky", "stiff", "still", "stitch", "stocking",
			"stomach", "stone", "stop", "store", "story", "straight",
			"strange", "street", "stretch", "strong", "structure", "substance",
			"such", "sudden", "sugar", "suggestion", "summer", "sun",
			"support", "surprise", "sweet", "swim", "system", "table", "tail",
			"take", "talk", "tall", "taste", "tax", "teaching", "tendency",
			"test", "than", "that", "the", "then", "theory", "there", "thick",
			"thin", "thing", "this", "thought", "thread", "throat", "through",
			"through", "thumb", "thunder", "ticket", "tight", "till", "time",
			"tin", "tired", "to", "toe", "together", "tomorrow", "tongue",
			"tooth", "top", "touch", "town", "trade", "train", "transport",
			"tray", "tree", "trick", "trouble", "trousers", "true", "turn",
			"twist", "umbrella", "under", "unit", "up", "use", "value",
			"verse", "very", "vessel", "view", "violent", "voice", "waiting",
			"walk", "wall", "war", "warm", "wash", "waste", "watch", "water",
			"wave", "wax", "way", "weather", "week", "weight", "well", "west",
			"wet", "wheel", "when", "where", "while", "whip", "whistle",
			"white", "who", "why", "wide", "will", "wind", "window", "wine",
			"wing", "winter", "wire", "wise", "with", "woman", "wood", "wool",
			"word", "work", "worm", "wound", "writing", "wrong", "year",
			"yellow", "yes", "yesterday", "you", "young" };
}

