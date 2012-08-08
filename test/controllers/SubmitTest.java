package controllers;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.tdl.vireo.model.*;
import org.tdl.vireo.security.SecurityContext;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test all the actions in the Submit controller.
 *
 * @author Dan Galewsky</a>
 */
public class SubmitTest extends AbstractVireoFunctionalTest {

    // Spring dependencies
    public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
    public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
    public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
    public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);

    @Test
    public void testFullSubmission() {    	
        
        LOGIN();

        // Create submission
        Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
        context.login(person);
        Submission s = subRepo.createSubmission(person);
        s.save();
        long subId = s.getId();

        JPA.em().getTransaction().commit();
        JPA.em().clear();
        JPA.em().getTransaction().begin();

        // Get first page
        String verifyURL = Router.reverse("Submit.verifyPersonalInformation").url;
        Response response = GET(verifyURL);
        assertIsOk(response);
        response = null;

        // Add valid values
        Map<String, String> args = new HashMap<String, String>();
        args.put("firstName", "TestStudentFirstName");
        args.put("middleName", "Middle");
        args.put("lastName", "TestStudentLastName");
        args.put("email", "test@studentemail.com");
        args.put("yearOfBirth", "1996");

        // Grab the first configured department, degree, major
        args.put("department", settingRepo.findAllDepartments().get(0).getName());
        args.put("degree", settingRepo.findAllDegrees().get(0).getName());
        args.put("major", settingRepo.findAllMajors().get(0).getName());

        args.put("permPhone", "555-1212");
        args.put("permAddress", "2222 Fake Street");
        args.put("permEmail", "noreply@noreply.org");
        args.put("currentPhone", "555-1212");
        args.put("currentAddress", "2222 Fake Street");

        args.put("subId", Long.toString(subId));
        args.put("submit_next", "");

        // Submit first page
        response = POST(verifyURL, args);
        response = GET(response.getHeader("Location"));

        // Check second page
        assertIsOk(response);
        assertContentMatch("License Agreement", response);

        // Check db
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
        s = null;
        s = subRepo.findSubmission(subId);

        // FIXME:  These values are currently not being persisted due to the locked field-set
        // assertEquals(args.get("firstName"), s.getStudentFirstName());
        // assertEquals(args.get("middleName"), s.getStudentMiddleName());
        // assertEquals(args.get("lastName"), s.getStudentLastName());
        // assertEquals(args.get("yearOfBirth"), Integer.toString(s.getStudentBirthYear()));

        assertEquals(args.get("department"), s.getDepartment());
        assertEquals(args.get("major"), s.getMajor());
        assertEquals(args.get("permPhone"), s.getSubmitter().getPermanentPhoneNumber());
        assertEquals(args.get("permAddress"), s.getSubmitter().getPermanentPostalAddress());
        assertEquals(args.get("permEmail"), s.getSubmitter().getPermanentEmailAddress());
        assertEquals(args.get("currentPhone"), s.getSubmitter().getCurrentPhoneNumber());
        assertEquals(args.get("currentAddress"), s.getSubmitter().getCurrentPostalAddress());        

        // Add valid values
        response = null;
        args = null;
        args = new HashMap<String, String>();
        args.put("submit_next", "");
        args.put("licenseAgreement", "on");

        // Submit second page
        Map<String, Object> routeArgs = new HashMap<String, Object>();
        routeArgs.put("subId", Long.toString(subId));
        String licenseURL = Router.reverse("Submit.license", routeArgs).url;
        response = POST(licenseURL, args);
        response = GET(response.getHeader("Location"));

        // Check third page
        assertIsOk(response);
        assertContentMatch("Document Information", response);

        // Check db
        response = null;
        s = null;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
        s = subRepo.findSubmission(subId);
        assertNotNull(s.getLicenseAgreementDate());

        // Add valid values
        args = null;
        args = new HashMap<String, String>();
        args.put("subId", Long.toString(subId));
        args.put("title", "Test Title");
        args.put("degreeMonth", String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()));
        args.put("degreeYear", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        args.put("docType", settingRepo.findDocumentType(Long.parseLong("1")).getName());
        args.put("abstractText", "This is an abstract");
        args.put("keywords", "key; word;");
        args.put("committeeFirstName1", "First");
        args.put("committeeMiddleName1", "Middle");
        args.put("committeeLastName1", "Last");
        args.put("committeeChairFlag1", "checked");
        args.put("chairEmail", "fake@email.com");
        args.put("embargo", "1");
        args.put("submit_next", "");

        // Submit third page
        String docInfoURL = Router.reverse("Submit.docInfo", routeArgs).url;
        response = POST(docInfoURL, args);
        response = GET(response.getHeader("Location"));

        // check fourth page
        assertIsOk(response);
        assertContentMatch("Upload Your Files", response);

        // check db
        s = null;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
        s = subRepo.findSubmission(subId);
        assertEquals(args.get("chairEmail"), s.getCommitteeContactEmail());
        assertEquals(args.get("title"), s.getDocumentTitle());
        assertEquals(args.get("degreeMonth"), s.getGraduationMonth().toString());
        assertEquals(args.get("degreeYear"), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        assertEquals(args.get("docType"), s.getDocumentType());
        assertEquals(args.get("abstractText"), s.getDocumentAbstract());
        assertEquals(args.get("keywords"), s.getDocumentKeywords());
        assertEquals(settingRepo.findEmbargoType(Long.parseLong(args.get("embargo").toString())), s.getEmbargoType());

        List<CommitteeMember> committeeMembers = s.getCommitteeMembers();
        assertEquals(1, committeeMembers.size());

        // Add valid values
        Map<String, File> files = new HashMap<String, File>();
        File thesisFile = null;

        try {
            thesisFile = getResourceFile("controllers/test.pdf");
            files.put("primaryDocument", thesisFile);

        } catch (IOException ioe) {
            fail("Test upload file not found");
        }

        args = null;
        args = new HashMap<String, String>();
        args.put("subId", Long.toString(subId));
        // FIXME: This page uses a dash in "submit-next". Correct to underscore to match all other pages.
        args.put("submit-next", "");

        // Submit fourth page
        String uploadURL = Router.reverse("Submit.fileUpload", routeArgs).url;
        response = POST(uploadURL, args, files);
        response = GET(response.getHeader("Location"));

        // Check fifth page
        assertIsOk(response);
        assertContentMatch("Confirm & Submit", response);

        // Check for file, db
        s = null;
        args = null;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
        s = subRepo.findSubmission(subId);
        assertNotNull(s.getPrimaryDocument());
        assertEquals(2, s.getAttachments().size());
        
        // FIXME: Degree not matching on Confirm & Submit page
        // assertContentMatch(s.getDegree(), response);
        
        // FIXME: School on Confirm & Submit page is hard-coded to "Texas Digital Library"
        // assertContentMatch(s.getCollege(), response);
        
        // FIXME: The Confirm & Submit page pulls the submitter object's "current major", not the one associated with the submission
        // assertContentMatch(s.getMajor(), response);
        
        assertContentMatch(s.getDocumentTitle(), response);
        assertContentMatch(s.getGraduationMonth().toString(), response);
        assertContentMatch(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), response);
        assertContentMatch(s.getDocumentType(), response);
        assertContentMatch(s.getDocumentKeywords(), response);
        assertContentMatch(s.getDocumentAbstract(), response);
        assertContentMatch(s.getCommitteeContactEmail(), response);
        assertContentMatch(s.getEmbargoType().getDescription(), response);
        assertContentMatch(s.getPrimaryDocument().getName(), response);
        
        // Add values
        args = new HashMap<String, String>();
        args.put("submit-next", "");
        args.put("subId", Long.toString(subId));

        // Submit fifth page
        String submitURL = Router.reverse("Submit.submitETD", routeArgs).url;
        response = POST(submitURL, args);
        response = GET(response.getHeader("Location"));

        // Check sixth page
        assertIsOk(response);
        assertContentMatch("Submittal Complete", response);

        // Check db
        s = null;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
        s = subRepo.findSubmission(subId);
        assertEquals("Submitted", s.getState().getDisplayName());

        // Delete submission & test file
        thesisFile.delete();

        s.delete();

        JPA.em().getTransaction().commit();
        JPA.em().clear();
        JPA.em().getTransaction().begin();

        // Verify deletion
        s = subRepo.findSubmission(subId);
        assertNull(s);
    }

    /**
     * Extract the file from the jar and place it in a temporary location for the test to operate from.
     *
     * @param filePath The path, relative to the classpath, of the file to reference.
     * @return A Java File object reference.
     * @throws IOException
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