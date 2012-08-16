package controllers.submit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;

/**
 * These are a set of tests which test all the submission processes. This test
 * file is structured to hopefully make it easier to create additional tests in
 * the future. For each of the submission steps there exists an internal helper
 * method that will submit data to the form, and then verify that the submission
 * was updated correctly.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Dan Galewsky
 * @author Sands Fish
 * 
 */
public class SubmissionTests extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);

	// A regular expression to extract the subid from the url.
	public static Pattern SUBMISSION_ID_PATTERN = Pattern.compile("^/submit/([0-9]+)/.*$");

	// The submission id if the currently being worked on submission.
	public Long subId = null;

	// The original configuration, we will restore to these after the test.
	public String originalRequestBirth = null;
	public String originalRequestCollege = null;
	public String originalRequestUMI = null;
	public String originalAllowMultiple = null;

	/**
	 * Setup
	 * 
	 * Grab the original configuration settings, and store their values.
	 */
	@Before
	public void setup() {
		// Get the configuration setting prior to doing anything so that we can restore them after.
		originalRequestBirth = settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH);
		originalRequestCollege = settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE);
		originalRequestUMI = settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI);
		originalAllowMultiple = settingRepo.getConfig(Configuration.ALLOW_MULTIPLE_SUBMISSIONS);

		// Turn off authentication for the test thread
		context.turnOffAuthorization();
	}

	/**
	 * Cleanup
	 * 
	 * Restore the original configuration settings, and delete the submission.
	 */
	@After
	public void cleanup() {

		// Restore our configuration.		
		Configuration requestBirth = settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_BIRTH);
		if (originalRequestBirth == null && requestBirth != null) {
			requestBirth.delete();
		}
		if (originalRequestBirth != null && requestBirth == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_BIRTH,"true");
		}

		Configuration requestCollege = settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_COLLEGE);
		if (originalRequestCollege == null && requestCollege != null) {
			requestCollege.delete();
		}
		if (originalRequestCollege != null && requestCollege == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_COLLEGE,"true");
		}

		Configuration requestUMI = settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_UMI);
		if (originalRequestUMI == null && requestUMI != null) {
			requestUMI.delete();
		}
		if (originalRequestUMI != null && requestUMI == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_UMI,"true");
		}
		Configuration allowMultiple = settingRepo.findConfigurationByName(Configuration.ALLOW_MULTIPLE_SUBMISSIONS);
		if (originalAllowMultiple == null && allowMultiple != null) {
			allowMultiple.delete();
		}
		if (originalRequestUMI != null && allowMultiple == null) {
			settingRepo.createConfiguration(Configuration.ALLOW_MULTIPLE_SUBMISSIONS,"true");
		}

		// if we created a submission, delete it.
		if (subId != null) {
			JPA.em().getTransaction().commit();
			JPA.em().clear();
			JPA.em().getTransaction().begin();
			subRepo.findSubmission(subId).delete();
		}
		
		context.restoreAuthorization();
	}

	/**
	 * Test a complete submission workflow without asking for any additional parameters.
	 */
	@Test
	public void testFullSubmission() throws IOException {    

		// Turn off any of the extra paramaters
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) != null) {
			settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_BIRTH).delete();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) != null) {
			settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_COLLEGE).delete();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) != null) {
			settingRepo.findConfigurationByName(Configuration.SUBMIT_REQUEST_UMI).delete();
		}
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String STATUS_URL = Router.reverse("Student.submissionStatus").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;


		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(STATUS_URL,response); // and it's url.

		response = GET(STATUS_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));
		response = GET(PERSONAL_INFO_URL);
		assertContentMatch("<title>Verify Personal Information</title>",response);

		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				null, // birthYear
				null, // college
				settingRepo.findAllDepartments().get(0).getName(), // department 
				settingRepo.findAllDegrees().get(0).getName(), // degree
				settingRepo.findAllMajors().get(0).getName(), // major 
				"555-1212", // permPhone
				"2222 Fake Street", // permAddress 
				"noreply@noreply.org", // permEmail
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
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				committee, // committee
				"noreplyfromadvisor@noreply.org", // committeeEmail
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				null // UMI
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc", "SampleSupplementalDocument.xls");

		// Finaly, confirm
		confirm();

		// the cleanup will make sure the submission gets deleted.
	}

	/**
	 * Test a full submission with all the additional paramaters turned on:
	 * birth year, college, and umi release.
	 */
	@Test
	public void testFullSubmissionWithOptionalParamaters() throws IOException {    

		// Turn ON any of the extra paramaters
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_BIRTH,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_COLLEGE,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_UMI,"true").save();
		}
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();


		// Login as the student Clair Danes
		LOGIN("cdanes@gmail.com");

		// Get our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String STATUS_URL = Router.reverse("Student.submissionStatus").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;

		// View the homepage
		Response response = GET(INDEX_URL);
		assertIsOk(response);
		assertContentMatch("Start your submission",response); // the start button is there.
		assertContentMatch(STATUS_URL,response); // and it's url.

		response = GET(STATUS_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));
		response = GET(PERSONAL_INFO_URL);
		assertContentMatch("<title>Verify Personal Information</title>",response);


		// PersonalInfo step
		personalInfo(
				null, // firstName
				"middle", // middleName
				null, // lastName
				"1971", // birthYear
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
				settingRepo.findAllDocumentTypes().get(0).getName(), // docType
				"This is really cool work!", // abstractText 
				"one; two; three;", // keywords
				committee, // committee
				"noreplyfromadvisor@noreply.org", // committeeEmail
				String.valueOf(settingRepo.findAllEmbargoTypes().get(1).getId()), // embargo
				"true" // UMI 
				);

		// FileUpload Step
		fileUpload("SamplePrimaryDocument.pdf", "SampleSupplementalDocument.doc");

		// Finaly, confirm
		confirm();

		// the cleanup will make sure the submission gets deleted.
	}
	
	/**
	 * Test weather multiple submissions are allowed.
	 */
	@Test
	public void testMultipleSubmissionsAllowed() throws IOException {    

		// Turn ON any of the extra paramaters
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_BIRTH,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_COLLEGE,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_UMI,"true").save();
		}
		if (settingRepo.getConfig(Configuration.ALLOW_MULTIPLE_SUBMISSIONS) == null) {
			settingRepo.createConfiguration(Configuration.ALLOW_MULTIPLE_SUBMISSIONS,"true").save();
		}
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
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_BIRTH,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_COLLEGE,"true").save();
		}
		if (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) == null) {
			settingRepo.createConfiguration(Configuration.SUBMIT_REQUEST_UMI,"true").save();
		}
		if (settingRepo.getConfig(Configuration.ALLOW_MULTIPLE_SUBMISSIONS) != null) {
			settingRepo.findConfigurationByName(Configuration.ALLOW_MULTIPLE_SUBMISSIONS).delete();
		}
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
	 * Handle the personal information step. If any value is null, then it will
	 * not be submitted with the form data. All non-null inputs will be verified
	 * that they were saved to the submission.
	 * 
	 * @param firstName
	 *            The student's first name.
	 * @param middleName
	 *            The student's middle name.
	 * @param lastName
	 *            The students's last name.
	 * @param birthYear
	 *            The student's birth year.
	 * @param college
	 * @param department
	 * @param degree
	 * @param major
	 * @param permPhone
	 * @param permAddress
	 * @param permEmail
	 * @param currentPhone
	 * @param currentAddress
	 */
	public void personalInfo(String firstName, String middleName, String lastName, String birthYear, String college, String department, String degree, String major, String permPhone, 
			String permAddress, String permEmail, String currentPhone, String currentAddress) {


		String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;
		if (subId != null) {
			// If we allready have a subId assigned then get the url scoped to that submission.
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("subId",subId);
			PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo",routeArgs).url;
		}


		// Add valid values
		Map<String, String> params= new HashMap<String, String>();
		if (firstName != null)
			params.put("firstName", firstName);
		if (middleName != null)
			params.put("middleName", middleName);
		if (lastName != null)
			params.put("lastName", lastName);
		if (birthYear != null)
			params.put("birthYear", birthYear);

		if (college != null)
			params.put("college", college);
		if (department != null)
			params.put("department", department);
		if (degree != null)
			params.put("degree", degree);
		if (major != null)
			params.put("major", major);
		if (permPhone != null)
			params.put("permPhone", permPhone);
		if (permAddress != null)
			params.put("permAddress", permAddress);
		if (permEmail != null)
			params.put("permEmail", permEmail);
		if (currentPhone != null)
			params.put("currentPhone", currentPhone);
		if (currentAddress != null)
			params.put("currentAddress", currentAddress);

		params.put("submit_next", "Save and Continue");

		// Submit the page
		Response response = POST(PERSONAL_INFO_URL, params);

		// Extract the submission id.
		String redirectURL = response.getHeader("Location");
		Matcher tokenMatcher = SUBMISSION_ID_PATTERN.matcher(redirectURL);
		assertTrue(tokenMatcher.find());
		String subIdString = tokenMatcher.group(1);
		assertNotNull(subIdString);
		Long foundSubId = Long.valueOf(subIdString);
		assertNotNull(foundSubId);
		assertTrue(foundSubId > 0);

		// If this is the first pass, save the subId.
		if (subId == null)
			subId = foundSubId;
		else
			assertEquals(subId,foundSubId);

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);
		Person submitter = sub.getSubmitter();

		// Verify all the data
		if (firstName == null) 
			// It should be the default value
			assertEquals(submitter.getFirstName(), sub.getStudentFirstName());
		else
			// Or it should be the value we supplied.
			assertEquals(firstName,sub.getStudentFirstName());

		if (middleName == null)
			assertEquals(submitter.getMiddleName(), sub.getStudentMiddleName());
		else
			assertEquals(middleName,sub.getStudentMiddleName());

		if (lastName == null)
			assertEquals(submitter.getLastName(), sub.getStudentLastName());
		else
			assertEquals(lastName,sub.getStudentLastName());

		if (birthYear == null)
			assertEquals(submitter.getBirthYear(), sub.getStudentBirthYear());
		else
			assertEquals(Integer.valueOf(birthYear),sub.getStudentBirthYear());


		// Academic Affiliation
		if (college == null)
			assertEquals(submitter.getCurrentCollege(), sub.getCollege());
		else
			assertEquals(college,sub.getCollege());

		if (department == null)
			assertEquals(submitter.getCurrentDepartment(),sub.getDepartment());
		else
			assertEquals(department,sub.getDepartment());

		if (major == null)
			assertEquals(submitter.getCurrentMajor(),sub.getMajor());
		else
			assertEquals(major, sub.getMajor());

		// Contact Information
		if (permPhone != null)
			assertEquals(permPhone,submitter.getPermanentPhoneNumber());
		if (permAddress != null)
			assertEquals(permAddress,submitter.getPermanentPostalAddress());
		if (permEmail != null)
			assertEquals(permEmail,submitter.getPermanentEmailAddress());
		if (currentPhone != null)
			assertEquals(currentPhone, submitter.getCurrentPhoneNumber());
		if (currentAddress != null)
			assertEquals(currentAddress, submitter.getCurrentPostalAddress());
	}

	/**
	 * Perform the license agreement step. The license will be agreed to, and
	 * the submission verified that it's state was updated. The license
	 * agreement date, and that a license attachment was added.
	 */
	public void license() {

		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String LICENSE_URL = Router.reverse("submit.License.license",routeArgs).url;

		// Build the form data
		Map<String, String> params= new HashMap<String, String>();
		params.put("licenseAgreement","checked");
		params.put("submit_next", "Save and Continue");

		// Post the form
		Response response = POST(LICENSE_URL,params);
		assertNotNull(response.getHeader("Location"));

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		// Check the license agreement date
		assertNotNull(sub.getLicenseAgreementDate());

		// Check for the license attachment. (it should be the only one)
		assertEquals(1,sub.getAttachments().size());
		assertEquals(AttachmentType.LICENSE,sub.getAttachments().get(0).getType());
	}

	/**
	 * Perform the document information step. All values are optionally null, if
	 * so they will not be supplied to the form. Any non-null values will be
	 * verified that they were updated on the submission.
	 * 
	 * @param title
	 *            The document title.
	 * @param degreeMonth
	 *            Graduation Month
	 * @param degreeYear
	 *            Graduation Year
	 * @param docType
	 *            Document Type
	 * @param abstractText
	 *            Document Abstract
	 * @param keywords
	 *            Document Keywords
	 * @param committee
	 *            List of maps of committee members.
	 * @param chairEmail
	 *            The committee contact email.
	 * @param embargo
	 *            The embargo settings.
	 * @param umi
	 *            UMI release flag.
	 */
	public void documentInfo(String title, String degreeMonth, String degreeYear, String docType, String abstractText, String keywords,
			List<Map<String,String>> committee, String chairEmail, String embargo, String umi)  {

		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String DOCUMENT_INFO_URL = Router.reverse("submit.DocumentInfo.documentInfo",routeArgs).url;

		// Build the form data
		Map<String, String> params= new HashMap<String, String>();
		if (title != null)
			params.put("title",title);
		if (degreeMonth != null)
			params.put("degreeMonth",degreeMonth);
		if (degreeYear != null)
			params.put("degreeYear",degreeYear);
		if (docType != null)
			params.put("docType", docType);
		if (abstractText != null)
			params.put("abstractText",abstractText);
		if (keywords != null)
			params.put("keywords",keywords);
		if (chairEmail != null)
			params.put("chairEmail", chairEmail);
		if (embargo != null)
			params.put("embargo", embargo);
		if (umi != null)
			params.put("umi",umi);
		for (int i = 0; i < committee.size(); i++) {
			Map<String,String> member = committee.get(i);

			params.put("committeeFirstName"+(i+1), member.get("firstName"));
			params.put("committeeLastName"+(i+1), member.get("lastName"));
			if (member.get("middleName") != null)
				params.put("committeeMiddleName"+(i+1), member.get("middleName"));
			if (member.get("chairFlag") != null)
				params.put("committeeChairFlag"+(i+1),"true");
		}
		params.put("submit_next", "Save and Continue");

		// Post the form
		Response response = POST(DOCUMENT_INFO_URL,params);
		assertNotNull(response.getHeader("Location")); 

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		if (title != null)
			assertEquals(title,sub.getDocumentTitle());
		if (degreeMonth != null)
			assertEquals(Integer.valueOf(degreeMonth),sub.getGraduationMonth());
		if (degreeYear != null)
			assertEquals(Integer.valueOf(degreeYear),sub.getGraduationYear());
		if (docType != null)
			assertEquals(docType,sub.getDocumentType());
		if (abstractText != null)
			assertEquals(abstractText,sub.getDocumentAbstract());
		if (keywords != null)
			assertEquals(keywords,sub.getDocumentKeywords());
		if (chairEmail != null)
			assertEquals(chairEmail, sub.getCommitteeContactEmail());
		if (embargo != null)
			assertEquals(Long.valueOf(embargo), sub.getEmbargoType().getId());
		if (umi != null)
			assertEquals(true, sub.getUMIRelease());

		assertEquals(committee.size(), sub.getCommitteeMembers().size());
	}

	/**
	 * Upload documents to the submission.
	 * 
	 * @param primary
	 *            The primary document to upload.
	 * @param supplementary
	 *            A list of supplementary documents to upload.
	 */
	public void fileUpload(String primary, String... supplementary)
			throws IOException {

		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String FILE_UPLOAD_URL = Router.reverse("submit.FileUpload.fileUpload",routeArgs).url;

		// Upload the primary documents
		Map<String, String> params= new HashMap<String, String>();
		params.put("uploadPrimary", "Upload");

		Map<String, File> fileParams = new HashMap<String,File>();
		File primaryFile = getResourceFile(primary);
		fileParams.put("primaryDocument", primaryFile);

		Response response = POST(FILE_UPLOAD_URL,params,fileParams);
		assertIsOk(response);
		primaryFile.delete();

		// Upload each of the supplementary documents
		for (String supplement : supplementary) {
			params= new HashMap<String, String>();
			params.put("uploadSupplementary", "Upload");

			fileParams = new HashMap<String,File>();
			File supplementaryFile = getResourceFile(supplement);
			fileParams.put("supplementaryDocument", supplementaryFile);

			response = POST(FILE_UPLOAD_URL,params,fileParams);
			assertIsOk(response);

			supplementaryFile.delete();
		}

		// Verify the submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		assertNotNull(sub.getPrimaryDocument());
		assertEquals(supplementary.length, sub.getSupplementalDocuments().size());


		// Now contiue on to the next page.
		params= new HashMap<String, String>();
		params.put("submit_next", "Save and Continue");

		// Post the form
		response = POST(FILE_UPLOAD_URL,params);
		assertNotNull(response.getHeader("Location")); 
	}

	/**
	 * Confirm the submission. We assume there will be no errors because each
	 * step before hand should have taken care of that.
	 */
	public void confirm() {

		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String CONFIRM_URL = Router.reverse("submit.Confirm.confirm",routeArgs).url;


		Map<String,String> params= new HashMap<String, String>();
		params.put("submit_confirm", "Confirm and Save Submission");

		// Post the form
		Response response = POST(CONFIRM_URL,params);
		assertNotNull(response.getHeader("Location")); 

		// Verify the submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		State newState = sub.getState();
		assertTrue(newState != stateManager.getInitialState());
		
		
		// Get the completted submission page.
		response = GET(response.getHeader("Location"));
		assertContentMatch("Submittal Complete", response);
	}



	/**
	 * Internal Helper Method
	 * 
	 * Extract the file from the jar and place it in a temporary location for
	 * the test to operate from. The caller needs to remember to delete the file
	 * after it's use.
	 * 
	 * @param filePath
	 *            The path, relative to the classpath, of the file to reference.
	 * @return A Java File object reference.
	 */
	protected static File getResourceFile(String filePath) throws IOException {

		File file = File.createTempFile("ingest-import-test", ".pdf");

		// While we're packaged by play we have to ask Play for the inputstream instead of the classloader.
		//InputStream is = DSpaceCSVIngestServiceImplTests.class
		//		.getResourceAsStream(filePath);
		InputStream is = Play.classloader.getResourceAsStream(filePath);
		OutputStream os = new FileOutputStream(file);

		// Copy the file out of the jar into a temporary space.
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		is.close();
		os.close();

		return file;
	}
}