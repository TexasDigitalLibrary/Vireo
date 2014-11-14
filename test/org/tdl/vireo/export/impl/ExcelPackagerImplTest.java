package org.tdl.vireo.export.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.search.impl.LuceneIndexerImpl;
import org.tdl.vireo.search.impl.LuceneSearcherImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the excel export for submission
 * 
 * 
 * @author william_mckinney@harvard.edu
 */
public class ExcelPackagerImplTest extends UnitTest {

    // Spring injection
    public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
    public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
    public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
    public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
    public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
    public static Indexer indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
    public static Searcher searcher = Spring.getBeanOfType(LuceneSearcherImpl.class);

    // Common state
    public Person person;

    /**
     * Setup before running any tests, clear out the job queue, and create a test person.
     * 
     */
    @Before
    public void setup() throws InterruptedException {
        indexer.rebuild(true);
        assertFalse(indexer.isJobRunning());

        context.login(MockPerson.getAdministrator());

        person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
//        JPA.em().getTransaction().commit();
//        JPA.em().getTransaction().begin();
    }

    /**
     * Cleanup by committing the database transaction and deleted the person we created before. Also clear out anything in the index queue before carrying on.
     */
    @After
    public void cleanup() throws InterruptedException {
        JPA.em().clear();
        if (person != null)
            personRepo.findPerson(person.getId()).delete();
        context.logout();

//        JPA.em().getTransaction().commit();
//        JPA.em().getTransaction().begin();

        indexer.rebuild(true);
        assertFalse(indexer.isJobRunning());
    }

    /**
     * 
     * Add two submissions (rows 11 and 12) and check that the titles are found in the proper cells.
     * 
     */
    @Test
    public void testSubmissionFilterExport() throws InterruptedException {
        Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.REVIEWER).save();

        EmbargoType embargo1 = settingRepo.createEmbargoType("embargo1", "description", 12, true).save();
        EmbargoType embargo2 = settingRepo.createEmbargoType("embargo2", "description", 24, true).save();

        Submission sub1 = subRepo.createSubmission(person);
        createSubmission(sub1, "B Title", "This is really important work", "One; Two; Three;", "committee@email.com", "degree", "department", "program", "college", "major", "documentType", 2002, 5, true);
        sub1.setAssignee(otherPerson);
        sub1.setSubmissionDate(new Date(2012, 5, 1));
        sub1.addEmbargoType(embargo2);
        sub1.setState(sub1.getState().getTransitions(sub1).get(0));
        sub1.save();

        Submission sub2 = subRepo.createSubmission(person);
        createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", "anotherCommittee@email.com", "another", "another", "another", "another", "another", "another", 2003, 6, null);
        sub2.setSubmissionDate(new Date(2005, 5, 1));
        sub2.addEmbargoType(embargo1);
        sub2.save();

        // Save our new submissions and add them to the index.
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();
        Thread.sleep(100);
        indexer.rebuild(true);
        assertFalse(indexer.isJobRunning());

        // empty filter
        NamedSearchFilter filter = subRepo.createSearchFilter(person, "test-empty").save();

        try {

            List<Submission> submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();

            // set default submission columns
            List<SearchOrder> columns = new ArrayList<SearchOrder>();
            columns.add(SearchOrder.ID);
            columns.add(SearchOrder.STUDENT_NAME);
            columns.add(SearchOrder.STATE);
            columns.add(SearchOrder.ASSIGNEE);
            columns.add(SearchOrder.DOCUMENT_TITLE);
            columns.add(SearchOrder.SUBMISSION_DATE);
            columns.add(SearchOrder.APPROVAL_DATE);
            columns.add(SearchOrder.EMBARGO_TYPE);

            // get the workbook
            ExcelPackagerImpl export = new ExcelPackagerImpl();
            XSSFWorkbook wb = export.testWorkbook(submissions, columns);

            // get the sheet
            Sheet sheet = wb.getSheet("vireo-export");
            assertTrue(null != sheet);

            // check the title cell values
            String sub1Title = sheet.getRow(11).getCell(4).getStringCellValue();
            String sub2Title = sheet.getRow(12).getCell(4).getStringCellValue();
            assertTrue("B Title".equals(sub1Title));
            assertTrue("A Title".equals(sub2Title));

        } finally {
            filter.delete();
            settingRepo.findEmbargoType(embargo1.getId()).delete();
            settingRepo.findEmbargoType(embargo2.getId()).delete();
            subRepo.findSubmission(sub1.getId()).delete();
            subRepo.findSubmission(sub2.getId()).delete();
            personRepo.findPerson(otherPerson.getId()).delete();
        }
    }

    /**
     * A short cut method for creating a submission.
     */
    private Submission createSubmission(Submission sub, String title, String docAbstract, String keywords, String committeeEmail, String degree, String department, String program, String college, String major, String documentType, Integer gradYear, Integer gradMonth, Boolean UMIRelease) {

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
