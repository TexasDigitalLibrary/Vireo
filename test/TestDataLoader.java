import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.security.impl.ShibbolethAuthenticationMethodImpl;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.*;
import play.modules.spring.Spring;

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
		"Texas A&M University at Qatar",
		"Undergraduate Honors Fellows",
		"Undergraduate Scholars"
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
	 * Generate Persons, Colleges, Departments, Majors,
	 * Degrees, Document Types and Graduation Months. 
	 */
	
	@Override
	public void doJob() {
		try {
			SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
			
			// Turn off authorizations.
			context.turnOffAuthorization(); 
			try {
				loadPeople();
				loadSettings();
			} finally {
				context.restoreAuthorization();
			}
			
			loadSubmissions();
			
			context.logout();
			
		} catch (Exception e) {Logger.error(e, "Unable to load test data.");}
	}
	
	
	
	public static void loadPeople() {
		
		PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
		ShibbolethAuthenticationMethodImpl shib = Spring.getBeanOfType(ShibbolethAuthenticationMethodImpl.class);
		
		// Create all persons
		for(PersonsArray personDefinition : PERSONS_DEFINITIONS) {
			Person person = personRepo.createPerson(personDefinition.netId, personDefinition.email, personDefinition.firstName, personDefinition.lastName, personDefinition.role);
			person.setPassword(personDefinition.password);
			person.save();
		}
		// Special case. Initialize Billy-bob with all the data defined by the shibboleth authentication. This results in a lot less confusion when the authentitation changes a person's metadat.
		
		boolean originalMock = shib.mock;
		shib.mock = true;
		shib.authenticate(null);
		shib.mock = originalMock;
		
	}
	
	public static void loadSettings() {
		
		SettingsRepository settingsRepo = Spring.getBeanOfType(SettingsRepository.class);
		
		// Create all colleges
		for(String collegeDefinition : COLLEGES_DEFINITIONS) {
			settingsRepo.createCollege(collegeDefinition).save();
		}
		
		// Create all departments
		for(String departmentDefinition : DEPARTMENTS_DEFINITIONS) {
			settingsRepo.createDepartment(departmentDefinition).save();
		}
		
		// Create all majors
		for(String majorDefinition : MAJORS_DEFINITIONS) {
			settingsRepo.createMajor(majorDefinition).save();
		}
		
		// Create all degrees
		for(DegreeLevelArray degreeDefinition : DEGREES_DEFINITIONS) {
			settingsRepo.createDegree(degreeDefinition.name, degreeDefinition.degreeLevel).save();
		}
		
		// Create all document types
		for(DegreeLevelArray docTypeDefinition : DOCTYPES_DEFINITIONS) {
			settingsRepo.createDocumentType(docTypeDefinition.name, docTypeDefinition.degreeLevel).save();
		}
		
		// Create all graduation months
		for(int gradMonthDefinition : GRAD_MONTHS_DEFINITIONS) {
			settingsRepo.createGraduationMonth(gradMonthDefinition).save();
		}
		
		// Create all embargo types
		for(EmbargoArray embargoDefinition : EMBARGO_DEFINTITIONS) {
			settingsRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active).save();
		}
	}
	
	public static void loadSubmissions() {
		
		PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
		SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		
		// Create several students to work with
		context.turnOffAuthorization();
		Person student1 = personRepo.createPerson("student1", "student1@tdl.org", "Student", "One", RoleType.STUDENT).save();
		Person student2 = personRepo.createPerson("student2", "student2@tdl.org", "Student", "Two", RoleType.STUDENT).save();
		Person student3 = personRepo.createPerson("student3", "student3@tdl.org", "Student", "Three", RoleType.STUDENT).save();
		Person student4 = personRepo.createPerson("student4", "student4@tdl.org", "Student", "Four", RoleType.STUDENT).save();
		Person student5 = personRepo.createPerson("student5", "student5@tdl.org", "Student", "Five", RoleType.STUDENT).save();
		Person student6 = personRepo.createPerson("student6", "student6@tdl.org", "Student", "Six", RoleType.STUDENT).save();
		Person student7 = personRepo.createPerson("student7", "student7@tdl.org", "Student", "Seven", RoleType.STUDENT).save();
		Person student8 = personRepo.createPerson("student8", "student8@tdl.org", "Student", "Eight", RoleType.STUDENT).save();
		Person student9 = personRepo.createPerson("student9", "student9@tdl.org", "Student", "Nine", RoleType.STUDENT).save();
		
		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		context.restoreAuthorization();
		
		Submission sub1 = subRepo.createSubmission(student1);
		sub1.setStudentFirstName("Student");
		sub1.setStudentLastName("One");
		sub1.setStudentBirthYear(1992);
		sub1.setDocumentTitle("A Generalized Study of Acids and Solids");
		sub1.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub1.setEmbargoType(embargos.get(0));
		sub1.addCommitteeMember("John", "Leggett", null, true);
		sub1.addCommitteeMember("Frank", "Shipman", null, false);
		sub1.addCommitteeMember("Andruid", "Kerne", null, false);
		sub1.setCommitteeContactEmail("committee@tdl.org");
		sub1.setSubmissionDate(new Date(2005-1900,05,01));
		sub1.setDegree(DEGREES_DEFINITIONS[0].name);
		sub1.setDepartment(DEPARTMENTS_DEFINITIONS[0]);
		sub1.setCollege(COLLEGES_DEFINITIONS[0]);
		sub1.setMajor(MAJORS_DEFINITIONS[0]);
		sub1.setDocumentType(DOCTYPES_DEFINITIONS[0].name);
		sub1.setGraduationYear(2010);
		sub1.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[0]);
		sub1.setUMIRelease(false);
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(student2);
		sub2.setStudentFirstName("Student");
		sub2.setStudentLastName("Two");
		sub2.setStudentBirthYear(1993);
		sub2.setDocumentTitle("Multi-scale properties and their relationship to sensitivities.");
		sub2.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub2.setEmbargoType(embargos.get(0));
		sub2.addCommitteeMember("Frank", "Shipman", null, true);
		sub2.addCommitteeMember("Andruid", "Kerne", null, false);
		sub2.addCommitteeMember("John", "Leggett", null, false);
		sub2.setCommitteeContactEmail("committee@tdl.org");
		sub2.setSubmissionDate(new Date(2010-1900,01,01));
		sub2.setDegree(DEGREES_DEFINITIONS[1].name);
		sub2.setDepartment(DEPARTMENTS_DEFINITIONS[1]);
		sub2.setCollege(COLLEGES_DEFINITIONS[1]);
		sub2.setMajor(MAJORS_DEFINITIONS[1]);
		sub2.setDocumentType(DOCTYPES_DEFINITIONS[1].name);
		sub2.setGraduationYear(2011);
		sub2.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[1]);
		sub2.setUMIRelease(true);
		sub2.save();
		
		Submission sub3 = subRepo.createSubmission(student3);
		sub3.setStudentFirstName("Student");
		sub3.setStudentLastName("Three");
		sub3.setStudentBirthYear(1994);
		sub3.setDocumentTitle("Algorithms That Will Ultimitaly Rule the World.");
		sub3.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub3.setEmbargoType(embargos.get(1));
		sub3.addCommitteeMember("Andruid", "Kerne", null, true);
		sub3.addCommitteeMember("John", "Leggett", null, false);
		sub3.addCommitteeMember("Frank", "Shipman", null, false);
		sub3.setCommitteeContactEmail("committee@tdl.org");
		sub3.setSubmissionDate(new Date(2011-1900,05,29));
		sub3.setDegree(DEGREES_DEFINITIONS[2].name);
		sub3.setDepartment(DEPARTMENTS_DEFINITIONS[2]);
		sub3.setCollege(COLLEGES_DEFINITIONS[2]);
		sub3.setMajor(MAJORS_DEFINITIONS[2]);
		sub3.setDocumentType(DOCTYPES_DEFINITIONS[2].name);
		sub3.setGraduationYear(2012);
		sub3.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[2]);
		sub3.setUMIRelease(true);
		sub3.save();
		
		Submission sub4 = subRepo.createSubmission(student4);
		sub4.setStudentFirstName("Student");
		sub4.setStudentLastName("Four");
		sub4.setStudentBirthYear(1995);
		sub4.setDocumentTitle("Finding simplicity in complex titles");
		sub4.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub4.setEmbargoType(embargos.get(2));
		sub4.addCommitteeMember("Bill", "Gates", "henery", true);
		sub4.addCommitteeMember("Steve", "Jobs", null, true);
		sub4.setCommitteeContactEmail("committee@tdl.org");
		sub4.setSubmissionDate(new Date(2011-1900,05,2));
		sub4.setDegree(DEGREES_DEFINITIONS[3].name);
		sub4.setDepartment(DEPARTMENTS_DEFINITIONS[20]);
		sub4.setCollege(COLLEGES_DEFINITIONS[10]);
		sub4.setMajor(MAJORS_DEFINITIONS[30]);
		sub4.setDocumentType(DOCTYPES_DEFINITIONS[3].name);
		sub4.setGraduationYear(2012);
		sub4.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[2]);
		sub4.setUMIRelease(false);
		sub4.save();
		
		Submission sub5 = subRepo.createSubmission(student5);
		sub5.setStudentFirstName("Student");
		sub5.setStudentLastName("Five");
		sub5.setStudentBirthYear(1995);
		sub5.setDocumentTitle("Base Isolation of Chilean Masonry: A Comparative Study");
		sub5.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub5.setEmbargoType(embargos.get(2));
		sub5.addCommitteeMember("Gary", "Ross", "henery", true);
		sub5.addCommitteeMember("Suzanne", "Collins", null, false);
		sub5.addCommitteeMember("Willow","Shields", null, false);
		sub5.addCommitteeMember("Johs", "Hutcherson", null, false);
		sub5.setCommitteeContactEmail("committee@tdl.org");
		sub5.setSubmissionDate(new Date(2011-1900,05,2));
		sub5.setDegree(DEGREES_DEFINITIONS[3].name);
		sub5.setDepartment(DEPARTMENTS_DEFINITIONS[21]);
		sub5.setCollege(COLLEGES_DEFINITIONS[5]);
		sub5.setMajor(MAJORS_DEFINITIONS[29]);
		sub5.setDocumentType(DOCTYPES_DEFINITIONS[3].name);
		sub5.setGraduationYear(2012);
		sub5.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[1]);
		sub5.setUMIRelease(false);
		sub5.save();
		
		Submission sub6 = subRepo.createSubmission(student6);
		sub6.setStudentFirstName("Student");
		sub6.setStudentLastName("Six");
		sub6.setStudentBirthYear(1995);
		sub6.setDocumentTitle("Numasists, have they lost all their cents?");
		sub6.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub6.setEmbargoType(embargos.get(2));
		sub6.addCommitteeMember("Seneca", "Crane", null, true);
		sub6.addCommitteeMember("Katniss", "Everdeen", null, false);
		sub6.addCommitteeMember("Primrose","Everdeen", null, false);
		sub6.addCommitteeMember("Hob", "Vendor", null, false);
		sub6.setCommitteeContactEmail("committee@tdl.org");
		sub6.setSubmissionDate(new Date(2011-1900,11,2));
		sub6.setDegree(DEGREES_DEFINITIONS[10].name);
		sub6.setDepartment(DEPARTMENTS_DEFINITIONS[22]);
		sub6.setCollege(COLLEGES_DEFINITIONS[5]);
		sub6.setMajor(MAJORS_DEFINITIONS[29]);
		sub6.setDocumentType(DOCTYPES_DEFINITIONS[2].name);
		sub6.setGraduationYear(2012);
		sub6.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[1]);
		sub6.setUMIRelease(true);
		sub6.save();
		
		Submission sub7 = subRepo.createSubmission(student7);
		sub7.setStudentFirstName("Student");
		sub7.setStudentLastName("Seven");
		sub7.setStudentBirthYear(1996);
		sub7.setDocumentTitle("The Study of Morality in an Everchanging World");
		sub7.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub7.setEmbargoType(embargos.get(3));
		sub7.addCommitteeMember("John", "Leggett", null, true);
		sub7.addCommitteeMember("Frank", "Shipman", null, false);
		sub7.addCommitteeMember("Andruid", "Kerne", null, false);
		sub7.setCommitteeContactEmail("committee@tdl.org");
		sub7.setSubmissionDate(new Date(2011-1900,11,2));
		sub7.setDegree(DEGREES_DEFINITIONS[11].name);
		sub7.setDepartment(DEPARTMENTS_DEFINITIONS[22]);
		sub7.setCollege(COLLEGES_DEFINITIONS[5]);
		sub7.setMajor(MAJORS_DEFINITIONS[29]);
		sub7.setDocumentType(DOCTYPES_DEFINITIONS[2].name);
		sub7.setGraduationYear(2012);
		sub7.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[1]);
		sub7.setUMIRelease(true);
		sub7.save();
		
		Submission sub8 = subRepo.createSubmission(student8);
		sub8.setStudentFirstName("Student");
		sub8.setStudentLastName("Eight");
		sub8.setStudentBirthYear(1996);
		sub8.setDocumentTitle("The Complete History of the Future");
		sub8.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub8.setEmbargoType(embargos.get(4));
		sub8.addCommitteeMember("John", "Leggett", null, true);
		sub8.addCommitteeMember("Frank", "Shipman", null, false);
		sub8.addCommitteeMember("Andruid", "Kerne", null, false);
		sub8.setCommitteeContactEmail("committee@tdl.org");
		sub8.setSubmissionDate(new Date(2011-1900,11,2));
		sub8.setDegree(DEGREES_DEFINITIONS[12].name);
		sub8.setDepartment(DEPARTMENTS_DEFINITIONS[28]);
		sub8.setCollege(COLLEGES_DEFINITIONS[2]);
		sub8.setMajor(MAJORS_DEFINITIONS[30]);
		sub8.setDocumentType(DOCTYPES_DEFINITIONS[2].name);
		sub8.setGraduationYear(2012);
		sub8.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[2]);
		sub8.setUMIRelease(true);
		sub8.save();
		
		
		Submission sub9 = subRepo.createSubmission(student9);
		sub9.setStudentFirstName("Student");
		sub9.setStudentLastName("Nine");
		sub9.setStudentBirthYear(1996);
		sub9.setDocumentTitle("The Limits Librarianship: How Many Books Is Too Many?");
		sub9.setDocumentAbstract("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget rutrum quam. Donec hendrerit pellentesque metus, eget malesuada magna aliquam ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas quam ligula, interdum nec egestas pharetra, pulvinar id nisl. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vitae risus non neque viverra pulvinar. Aenean dictum laoreet eros sit amet lobortis. Suspendisse potenti.");
		sub9.setEmbargoType(embargos.get(4));
		sub9.addCommitteeMember("John", "Leggett", null, true);
		sub9.addCommitteeMember("Frank", "Shipman", null, false);
		sub9.addCommitteeMember("Andruid", "Kerne", null, false);
		sub9.setCommitteeContactEmail("committee@tdl.org");
		sub9.setSubmissionDate(new Date(2011-1900,11,2));
		sub9.setDegree(DEGREES_DEFINITIONS[10].name);
		sub9.setDepartment(DEPARTMENTS_DEFINITIONS[26]);
		sub9.setCollege(COLLEGES_DEFINITIONS[8]);
		sub9.setMajor(MAJORS_DEFINITIONS[31]);
		sub9.setDocumentType(DOCTYPES_DEFINITIONS[3].name);
		sub9.setGraduationYear(2012);
		sub9.setGraduationMonth(GRAD_MONTHS_DEFINITIONS[2]);
		sub9.setUMIRelease(true);
		sub9.save();

		Submission sub10 = subRepo.createSubmission(student1);
		// Everything is null on sub10
		sub10.save();
		
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
}

