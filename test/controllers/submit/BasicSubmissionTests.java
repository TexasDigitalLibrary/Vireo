package controllers.submit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.Submission;

import play.db.jpa.JPA;
import play.mvc.Router;
import play.mvc.Http.Response;

/**
 * Submission tests.
 * 
 * This class builds on the utility of the abstract submission tests to actually
 * test individual uses cases.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class BasicSubmissionTests extends AbstractSubmissionTests {

	/**
	 * Test that there is not an endless redirect bug when submissions are closed and a student has a submission in progress. This condition only occurs when multiple submissions are turned off.
	 */
	@Test
	public void testVIERO90() throws IOException, InterruptedException {    

		// Turn off any of the extra parameters
		enableFields(FieldConfig.values());
		disableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		disableFields(FieldConfig.COLLEGE);
		disableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		setSubmissionsOpen(false);
		
		// Create an in-progress submission
		Person cdanes = personRepo.findPersonByEmail("cdanes@gmail.com");
		Submission sub = subRepo.createSubmission(cdanes);
		sub.save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId", sub.getId());
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String DELETE_URL = Router.reverse("Student.submissionDelete",routeArgs).url;
		
		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertIsOk(response);
		// There should be a message telling the user that submissions are closed.
		assertContentMatch("Submissions are currently closed",response);
		assertContentMatch("This submission was not completed by the deadline. Please contact the thesis office.",response);
		
		// There should be no edit links.
		assertFalse(getContent(response).contains("Continue</a>"));
		
		// Delete the late submission
		response = GET(DELETE_URL);
		assertEquals(INDEX_URL,response.getHeader("Location"));
	}
	
	/**
	 * Test a complete submission workflow without asking for any additional parameters.
	 */
	@Test
	public void testFullSubmission() throws IOException, InterruptedException {    

		// Turn off any of the extra parameters
		enableFields(FieldConfig.values());
		disableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		disableFields(FieldConfig.COLLEGE);
		disableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				null, // birthYear
				null, // program
				null, // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"advisor@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();
		Map<String,String> member1 = new HashMap<String,String>();
		member1.put("firstName", "Bob");
		member1.put("lastName", "Jones");
		member1.put("chairFlag", "true");
		Map<String,String> member2 = new HashMap<String,String>();
		member2.put("firstName", "John");
		member2.put("middleName", "Jack");
		member2.put("lastName", "Leggett");
		committee.add(member1);
		committee.add(member2);

		documentInfo(
				"Clair Danes Thesis on Testing", // title
				String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()), // degreeMonth 
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), // degreeYear 
				"12/12/2012", // defenseDate
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				"Agriculture, Plant Pathology", // primary subject
				"Agriculture, Animal Pathology", // secondary subject
				"Agriculture, Food Science and Technology", // tertiary subject
				"en", // language
				committee, // committee
				"advisor@noreply.org", // committeeEmail
				"chapter #2 has published material", // publishedMaterial
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc", "SampleSupplementalDocument.xls");

		// Finaly, confirm
		confirm("cdanes@gmail.com","advisor@noreply.org");

		// the cleanup will make sure the submission gets deleted.
	}

	/**
	 * Test a full submission with all the additional paramaters turned on:
	 * birth year, college, and umi release.
	 */
	@Test
	public void testFullSubmissionWithOptionalParamaters() throws IOException, InterruptedException {    

		// Turn ON any of the extra paramaters
		enableFields(FieldConfig.values());
		enableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		enableFields(FieldConfig.COLLEGE);
		enableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();


		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;

		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));


		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				"1971", // birthYear
				settingRepo.findAllPrograms().get(0).getName(), // program
				settingRepo.findAllColleges().get(0).getName(), // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"perm@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();
		Map<String,String> member1 = new HashMap<String,String>();
		member1.put("firstName", "Bob");
		member1.put("lastName", "Jones");
		member1.put("chairFlag", "true");
		Map<String,String> member2 = new HashMap<String,String>();
		member2.put("firstName", "John");
		member2.put("middleName", "Jack");
		member2.put("lastName", "Leggett");
		committee.add(member1);
		committee.add(member2);

		documentInfo(
				"Clair Danes Thesis on Testing ", // title
				String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()), // degreeMonth 
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), // degreeYear
				null, // defenseDate
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				"Agriculture, Plant Pathology", // primary subject
				null, // secondary subject
				null, // tertiary subject
				null, // language
				committee, // committee
				"committee@noreply.org", // committeeEmail
				"chapter #2 has published material", // publishedMaterial
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				"true" // UMI 
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc");

		// Finaly, confirm
		confirm("cdanes@gmail.com","committee@noreply.org");

		// the cleanup will make sure the submission gets deleted.
	}
	
	/**
	 * Test submissions when there are no colleges, departments, or majors defined. These become free-form search fields.
	 */
	@Test
	public void testFullSubmissionWithNoPredefinedLists() throws IOException, InterruptedException {    

		// Turn off any of the extra paramaters
		enableFields(FieldConfig.values());
		disableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		disableFields(FieldConfig.COLLEGE);
		disableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		
		// clear out colleges, department, majors, programs, and languages
		List<String> originalPrograms = new ArrayList<String>();
		for (Program program : settingRepo.findAllPrograms()) {
			originalPrograms.add(program.getName());
			program.delete();
		}
		List<String> originalColleges = new ArrayList<String>();
		for (College college : settingRepo.findAllColleges()) {
			originalColleges.add(college.getName());
			college.delete();
		}
		List<String> originalDepartments = new ArrayList<String>();
		for (Department department : settingRepo.findAllDepartments()) {
			originalDepartments.add(department.getName());
			department.delete();
		}
		List<String> originalMajors = new ArrayList<String>();
		for (Major major : settingRepo.findAllMajors()) {
			originalMajors.add(major.getName());
			major.delete();
		}
		List<String> originalLanguages = new ArrayList<String>();
		for (Language language : settingRepo.findAllLanguages()) {
			originalLanguages.add(language.getName());
			language.delete();
		}
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				null, // birthYear
				"My Program", // program
				"My College", // college
				"My Department", // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				"My Major", // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"advisor@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();
		Map<String,String> member1 = new HashMap<String,String>();
		member1.put("firstName", "Bob");
		member1.put("lastName", "Jones");
		member1.put("chairFlag", "true");
		Map<String,String> member2 = new HashMap<String,String>();
		member2.put("firstName", "John");
		member2.put("middleName", "Jack");
		member2.put("lastName", "Leggett");
		committee.add(member1);
		committee.add(member2);

		documentInfo(
				"Clair Danes Thesis on Testing", // title
				String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()), // degreeMonth 
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), // degreeYear
				"12/12/2012", // defenseDate
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				"Agriculture, Plant Pathology", // primary subject
				"Agriculture, Animal Pathology", // secondary subject
				"Agriculture, Food Science and Technology", // tertiary subject
				null, // language
				committee, // committee
				"advisor@noreply.org", // committeeEmail
				"chapter #2 has published material", // publishedMaterial
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc", "SampleSupplementalDocument.xls");

		// Finaly, confirm
		confirm("cdanes@gmail.com","advisor@noreply.org");

		// Restore all Colleges, Departments, and Majors
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// clear out colleges, department, majors
		int i = 0;
		for (String name : originalPrograms) {
			Program program = settingRepo.createProgram(name);
			program.setDisplayOrder(i++);
			program.save();
		}
		for (String name : originalColleges) {
			College college = settingRepo.createCollege(name);
			college.setDisplayOrder(i++);
			college.save();
		}
		for (String name : originalDepartments) {
			Department department = settingRepo.createDepartment(name);
			department.setDisplayOrder(i++);
			department.save();
		}
		for (String name : originalMajors) {
			Major major = settingRepo.createMajor(name);
			major.setDisplayOrder(i++);
			major.save();
		}
		for (String name : originalLanguages) {
			Language language = settingRepo.createLanguage(name);
			language.setDisplayOrder(i++);
			language.save();
		}
	}
	

	/**
	 * Test a delaying the advisor email
	 */
	@Test
	public void testFullSubmissionWithoutAdvisorEmail() throws IOException, InterruptedException {    

		// Turn off any of the extra parameters
		enableFields(FieldConfig.values());
		disableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		disableFields(FieldConfig.COLLEGE);
		disableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		setDelayAdvisorEmail(true);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				null, // birthYear
				null, // program
				null, // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"advisor@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();
		Map<String,String> member1 = new HashMap<String,String>();
		member1.put("firstName", "Bob");
		member1.put("lastName", "Jones");
		member1.put("chairFlag", "true");
		Map<String,String> member2 = new HashMap<String,String>();
		member2.put("firstName", "John");
		member2.put("middleName", "Jack");
		member2.put("lastName", "Leggett");
		committee.add(member1);
		committee.add(member2);

		documentInfo(
				"Clair Danes Thesis on Testing", // title
				String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()), // degreeMonth 
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), // degreeYear 
				"12/12/2012", // defenseDate
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				"Agriculture, Plant Pathology", // primary subject
				"Agriculture, Animal Pathology", // secondary subject
				"Agriculture, Food Science and Technology", // tertiary subject
				"en", // language
				committee, // committee
				"advisor@noreply.org", // committeeEmail
				"chapter #2 has published material", // publishedMaterial
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc", "SampleSupplementalDocument.xls");

		// Finaly, confirm
		confirm("cdanes@gmail.com","!advisor@noreply.org");

		// the cleanup will make sure the submission gets deleted.
	}
	
	/**
	 * Test weather multiple submissions are allowed.
	 */
	@Test
	public void testMultipleSubmissionsAllowed() throws IOException {    

		// Turn ON any of the extra paramaters
		enableFields(FieldConfig.values());
		enableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		enableFields(FieldConfig.COLLEGE);
		enableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(true);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();


		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Create first submission
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				"1971", // birthYear
				settingRepo.findAllPrograms().get(0).getName(), // program
				settingRepo.findAllColleges().get(0).getName(), // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"noreply@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	
		
		// Attempt to create a second submission.
		Long firstId = subId;
		subId = null;
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				"1971", // birthYear
				settingRepo.findAllPrograms().get(0).getName(), // program
				settingRepo.findAllColleges().get(0).getName(), // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"noreply@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	
		
		assertTrue(subId != firstId);
		
		// Clean up the first submission, the other one will happen in cleanup.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		subRepo.findSubmission(firstId).delete();
	}
	
	/**
	 * Test weather multiple submissions are allowed.
	 */
	@Test
	public void testMultipleSubmissionsDisallowed() throws IOException {    

		// Turn ON any of the extra paramaters
		enableFields(FieldConfig.values());
		enableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		enableFields(FieldConfig.COLLEGE);
		enableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();


		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Create first submission
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				"1971", // birthYear
				settingRepo.findAllPrograms().get(0).getName(), // program
				settingRepo.findAllColleges().get(0).getName(), // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"noreply@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	
		
		// Attempt to create a second submission.
		String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;
		
		Response response = GET(PERSONAL_INFO_URL);
		// This should fail.
		assertEquals(new Integer(500),response.status);
	}
	
	/**
	 * Test a complete submission workflow without asking for any additional parameters.
	 */
	@Test
	public void testCommitteeMembers() throws IOException, InterruptedException {    

		// Turn off any of the extra paramaters
		enableFields(FieldConfig.values());
		disableFields(FieldConfig.STUDENT_BIRTH_YEAR);
		disableFields(FieldConfig.COLLEGE);
		disableFields(FieldConfig.UMI_RELEASE);
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				null, // birthYear
				null, // program
				null, // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"advisor@noreply.org", // permEmail
				"555-1212 ex2", // currentPhone 
				"2222 Fake Street APT 11" //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();
		Map<String,String> member1 = new HashMap<String,String>();
		member1.put("firstName", "Bob");
		member1.put("lastName", "Jones");
		Map<String,String> member2 = new HashMap<String,String>();
		member2.put("firstName", "John");
		member2.put("lastName", "Jack");
		member2.put("role", "Chair");
		committee.add(member1);
		committee.add(member2);

		documentInfo(
				"Clair Danes Thesis on Testing", // title
				String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()), // degreeMonth 
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), // degreeYear
				"12/12/2012", // defenseDate
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				"Agriculture, Plant Pathology", // primary subject
				"Agriculture, Animal Pathology", // secondary subject
				"Agriculture, Food Science and Technology", // tertiary subject
				"en", // language
				committee, // committee
				"advisor@noreply.org", // committeeEmail
				"chapter #2 has published material", // publishedMaterial
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				null // UMI
				);

		
		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String DOCUMENT_INFO_URL = Router.reverse("submit.DocumentInfo.documentInfo",routeArgs).url;

		// Build the form data
		Map<String, String> params= new HashMap<String, String>();
		params.put("title","Clair Danes Thesis on Testing");
		params.put("degreeMonth",String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()));
		params.put("degreeYear","2010");
		params.put("docType", settingRepo.findAllDocumentTypes().get(0).getName());
		params.put("abstractText","This is really cool work");
		params.put("keywords","one; two; three;");
		params.put("chairEmail", "advisor@noreply.org");
		params.put("embargo", String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()));

		params.put("committeeFirstName1", "Bob");
		params.put("committeeLastName1","Jones");
		params.put("committeeFirstName2", "John");
		params.put("committeeLastName2","Leggett");
		params.put("committeeMiddleName2", "J.");
		params.put("committeeRoles2", "Chair");
		
		params.put("step","documentInfo");
		params.put("submit_add", "Add Additional Members");

		// Post the form
		response = POST(DOCUMENT_INFO_URL,params);
		assertIsOk(response);

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		// We're just checking the commitee members.
		List<CommitteeMember> members = sub.getCommitteeMembers();
		
		assertEquals("Bob Jones", members.get(0).getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals(0,members.get(0).getRoles().size());
		assertEquals(1,members.get(0).getDisplayOrder());
		assertEquals("John J. Leggett", members.get(1).getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals(1,members.get(1).getRoles().size());
		assertEquals(2,members.get(1).getDisplayOrder());
		
		// Cleanup will delete the submission
	}
	
	
	/**
	 * Test a complete submission with everything set to be optional
	 */
	@Test
	public void testEverythingOptional() throws IOException, InterruptedException {    

		// Turn off any of the extra paramaters
		enableFields(FieldConfig.values());
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				null, // middleName
				null, // lastName
				null, // birthYear
				null, // program
				null, // college
				null, // department 
				null, // degree
				null, // major 
				null, // permPhone
				null, // permAddress 
				null, // permEmail
				null, // currentPhone 
				null //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();

		documentInfo(
				null, // title
				null, // degreeMonth 
				null, // degreeYear
				null, // defenseDate
				null, // docType
				null, // abstractText 
				null, // keywords
				null, // primary subject
				null, // secondary subject
				null, // tertiary subject
				null, // language
				committee, // committee
				null, // committeeEmail
				null, // publishedMaterial
				null, // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload(null);

		// Finaly, confirm
		confirm("cdanes@gmail.com",null);

		// the cleanup will make sure the submission gets deleted.
	}

	
	/**
	 * Test a complete submission with all fields turned off, literally every
	 * page is post nothing and go no the next page. And finally there will be
	 * no errors to confirm the submission.
	 */
	@Test
	public void testEverythingDisabled() throws IOException, InterruptedException {    

		// Turn off any of the extra paramaters
		disableFields(FieldConfig.values());
		setAllowMultipleSubmissions(false);
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(LIST_URL,response); // and it's url.

		response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));

		// PersonalInfo step
		personalInfo(
				null, // firstName
				null, // middleName
				null, // lastName
				null, // birthYear
				null, // program
				null, // college
				null, // department 
				null, // degree
				null, // major 
				null, // permPhone
				null, // permAddress 
				null, // permEmail
				null, // currentPhone 
				null //currentAddress
				);	

		// License Step
		license();

		// DocumentInfo Step
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();

		documentInfo(
				null, // title
				null, // degreeMonth 
				null, // degreeYear
				null, // defenseDate
				null, // docType
				null, // abstractText 
				null, // keywords
				null, // primary subject
				null, // secondary subject
				null, // tertiary subject
				null, // language
				committee, // committee
				null, // committeeEmail
				null, // publishedMaterial
				null, // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload(null);

		// Finaly, confirm
		confirm("cdanes@gmail.com",null);

		// the cleanup will make sure the submission gets deleted.
	}
	
}
