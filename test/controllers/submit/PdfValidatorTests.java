package controllers.submit;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.*;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test PDF validation of primary document PDF files
 *
 *
 * @author william_mckinney@harvard.edu
 */
public class PdfValidatorTests extends UnitTest{


    public static final File pdfFontsNotEmbedded = new File("test/controllers/submit/FontsNotEmbedded.pdf");
    public static final File validPdf = new File("test/SamplePrimaryDocument.pdf");
    public Person person;

    // Spring injection
    public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
    public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
    public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
    public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
    public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);

    /**
     * Setup before running any tests, clear out the job queue, and create a
     * test person.
     *
     */
    @Before
    public void setup() throws InterruptedException {

        context.login(MockPerson.getAdministrator());
        person = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.REVIEWER).save();
    }

    /**
     * Cleanup by committing the database transaction.
     */
    @After
    public void cleanup() throws InterruptedException {
        JPA.em().clear();
        context.logout();

        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();
    }

    /**
     *
     * Add a submission with a PDF that has non-embedded fonts
     *
     */
    @Test
    public void testNonEmbeddedFonts() throws InterruptedException {

        Submission sub = subRepo.createSubmission(person);

        createSubmission(sub, "B Title", "This is really important work", "One; Two; Three;",
                "committee@email.com", "degree", "department", "program", "college", "major",
                "documentType", 2002, 5, true);
        sub.setAssignee(person);
        sub.setSubmissionDate(new Date(2012, 5, 1));
        sub.setState(sub.getState().getTransitions(sub).get(0));

        try {

            sub.addAttachment(pdfFontsNotEmbedded, AttachmentType.PRIMARY);
            sub.save();

            // we should have just one, un-validated, attachment now
            assertTrue(sub.getAttachments().size() == 1);

            // run validator
            PdfValidator.doValidation(sub, Validation.current());

            // we should have one error message for the ArialMT font and no attachments now
            assertTrue(Validation.current().errors().size() == 1);
            assertTrue(Validation.current().errors().get(0).message().contains("One or more fonts used in your PDF are missing: ArialMT"));
            assertTrue(sub.getAttachments().size() == 0);

        } catch (IOException ioe) {
            Logger.error("Can't find the pdf test file: " + pdfFontsNotEmbedded.getAbsolutePath());
        }

        // Clean up the submission and person
        JPA.em().getTransaction().commit();
        JPA.em().clear();
        JPA.em().getTransaction().begin();
        subRepo.findSubmission(sub.getId()).delete();
        if (person != null)
            personRepo.findPerson(person.getId()).delete();
        Validation.clear();

    }

    /**
     *
     * Add a submission with a PDF that has non-embedded fonts
     *
     */
    @Test
    public void testValidPdf() throws InterruptedException {

        Submission sub = subRepo.createSubmission(person);

        createSubmission(sub, "B Title", "This is really important work", "One; Two; Three;",
                "committee@email.com", "degree", "department", "program", "college", "major",
                "documentType", 2002, 5, true);
        sub.setAssignee(person);
        sub.setSubmissionDate(new Date(2012, 5, 1));
        sub.setState(sub.getState().getTransitions(sub).get(0));

        try {

            sub.addAttachment(validPdf, AttachmentType.PRIMARY);
            sub.save();

            // we should have just one, un-validated, attachment now
            assertTrue(sub.getAttachments().size() == 1);

            // run validator
            PdfValidator.doValidation(sub, Validation.current());

            // we should have no error messages and one attachment
            assertTrue(Validation.current().errors().size() == 0);
            assertTrue(sub.getAttachments().size() == 1);

        } catch (IOException ioe) {
            Logger.error("Can't find the pdf test file: " + validPdf.getAbsolutePath());
        }

        // Clean up the submission and person
        JPA.em().getTransaction().commit();
        JPA.em().clear();
        JPA.em().getTransaction().begin();
        subRepo.findSubmission(sub.getId()).delete();
        if (person != null)
            personRepo.findPerson(person.getId()).delete();
        Validation.clear();

    }

    /**
     * A short cut method for creating a submission.
     */
    private Submission createSubmission(Submission sub, String title, String docAbstract,
                                        String keywords, String committeeEmail,
                                        String degree, String department, String program,
                                        String college, String major, String documentType,
                                        Integer gradYear, Integer gradMonth, Boolean UMIRelease) {

        sub.setDocumentTitle(title);
        sub.setDocumentAbstract(docAbstract);
        sub.setDocumentKeywords(keywords);
        sub.setCommitteeContactEmail(committeeEmail);
        sub.setCommitteeApprovalDate(new Date());
        sub.setDegree(degree);
        sub.setDepartment(department);
        sub.setProgram(program);
        sub.setCollege(college);
        sub.setMajor(major);
        sub.setDocumentType(documentType);
        sub.setGraduationYear(gradYear);
        sub.setGraduationMonth(gradMonth);
        sub.setUMIRelease(UMIRelease);

        return sub;
    }

}
