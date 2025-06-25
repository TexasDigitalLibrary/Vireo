package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.DegreeLevelRepo;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
@SpringBootTest(classes = { Application.class })
@Transactional(propagation = Propagation.REQUIRES_NEW)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SystemDataLoaderTest {

    @Autowired
    private SystemDataLoader systemDataLoader;

    @Autowired
    private EntityControlledVocabularyService entityControlledVocabularyService;

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private NoteRepo noteRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private DegreeLevelRepo degreeLevelRepo;

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Autowired
    private DocumentTypeRepo documentTypeRepo;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Autowired
    private AbstractPackagerRepo abstractPackagerRepo;

    @Test
    public void testLoadSystemData() throws Exception {
        assertBeforeLoadSystemData();

        systemDataLoader.loadSystemData();
        entityControlledVocabularyService.scanForEntityControlledVocabularies();
        assertAfterLoadSystemData(false);

        // reload to ensure nothing changes
        systemDataLoader.loadSystemData();
        entityControlledVocabularyService.scanForEntityControlledVocabularies();
        assertAfterLoadSystemData(true);
    }

    private void assertBeforeLoadSystemData() {
        assertEquals(0, inputTypeRepo.count());
        assertEquals(0, emailTemplateRepo.count());
        assertEquals(0, abstractEmailRecipientRepo.count());
        assertEquals(0, embargoRepo.count());
        assertEquals(0, organizationRepo.count());
        assertEquals(0, organizationCategoryRepo.count());
        assertEquals(0, workflowStepRepo.count());
        assertEquals(0, noteRepo.count());
        assertEquals(0, fieldProfileRepo.count());
        assertEquals(0, configurationRepo.count());
        assertEquals(0, fieldPredicateRepo.count());
        assertEquals(0, controlledVocabularyRepo.count());
        assertEquals(0, vocabularyRepo.count());
        assertEquals(0, languageRepo.count());
        assertEquals(0, degreeRepo.count());
        assertEquals(0, degreeLevelRepo.count());
        assertEquals(0, graduationMonthRepo.count());
        assertEquals(0, documentTypeRepo.count());
        assertEquals(0, emailWorkflowRuleRepo.count());
        assertEquals(0, submissionStatusRepo.count());
        assertEquals(0, submissionListColumnRepo.count());
        assertEquals(0, abstractPackagerRepo.count());
    }

    private void assertAfterLoadSystemData(boolean isReload) {
        assertInputType(isReload);
        assertEmailTemplate(isReload);
        assertAbstractEmailRecipient(isReload);
        assertEmbargo(isReload);
        assertOrganization(isReload);
        assertOrganizationCategory(isReload);
        assertWorkflowStep(isReload);
        assertNote(isReload);
        assertFieldProfile(isReload);
        assertConfiguration(isReload);
        assertFieldPredicate(isReload);
        assertControlledVocabulary(isReload);
        assertVocabulary(isReload);
        assertLanguage(isReload);
        assertDegree(isReload);
        assertDegreeLevel(isReload);
        assertGraduationMonth(isReload);
        assertDocumentType(isReload);
        assertEmailWorkflowRule(isReload);
        assertSubmissionStatus(isReload);
        assertSubmissionListColumn(isReload);
        assertAbstractPackager(isReload);
    }

    private void assertInputType(boolean isReload) {
        assertEquals(18, inputTypeRepo.count(),
            isReload
                ? "Incorrect number of inputType found after reload"
                : "Incorrect number of inputType found");
    }

    private void assertEmailTemplate(boolean isReload) {
        assertEquals(19, emailTemplateRepo.count(),
            isReload
                ? "Incorrect number of emailTemplate found after reload"
                : "Incorrect number of emailTemplate found");
    }

    private void assertAbstractEmailRecipient(boolean isReload) {
        assertEquals(3, abstractEmailRecipientRepo.count(),
            isReload
                ? "Incorrect number of abstractEmailRecipient found after reload"
                : "Incorrect number of abstractEmailRecipient found");
    }

    private void assertEmbargo(boolean isReload) {
        assertEquals(9, embargoRepo.count(),
            isReload
                ? "Incorrect number of embargo found after reload"
                : "Incorrect number of embargo found");
    }

    private void assertOrganization(boolean isReload) {
        assertEquals(1, organizationRepo.count(),
            isReload
                ? "Incorrect number of organization found after reload"
                : "Incorrect number of organization found");

        Organization institution = organizationRepo.findAll().get(0);

        assertEquals(false, institution.getAcceptsSubmissions(),
            isReload
                ? "Organization has incorrect accepts submission flag after reload"
                : "Organization has incorrect accepts submission flag");

        assertEquals(4, institution.getOriginalWorkflowSteps().size(),
            isReload
                ? "Organization has incorrect number original workflow steps after reload"
                : "Organization has incorrect number original workflow steps");

        assertEquals(4, institution.getAggregateWorkflowSteps().size(),
            isReload
                ? "Organization has incorrect number aggregate workflow steps after reload"
                : "Organization has incorrect number aggregate workflow steps");

        assertEquals(0, institution.getAncestorOrganizations().size(),
            isReload
                ? "Organization has ancestor organizations after reload"
                : "Organization has ancestor organizations");

        assertNull(institution.getParentOrganization(),
            isReload
                ? "Organization has parent organization after reload"
                : "Organization has parent organization");

        assertEquals(0, institution.getChildrenOrganizations().size(),
            isReload
                ? "Organization has children organizations after reload"
                : "Organization has children organizations");

        assertEquals(0, institution.getEmails().size(),
            isReload
                ? "Organization has emails after reload"
                : "Organization has emails");

        assertEquals(2, institution.getEmailWorkflowRules().size(),
            isReload
                ? "Organization has incorrect number email workflow rules after reload"
                : "Organization has incorrect number email workflow rules");

        assertEquals(2, institution.getAggregateEmailWorkflowRules().size(),
            isReload
                ? "Organization has incorrect number aggregate email workflow rules after reload"
                : "Organization has incorrect number aggregate email workflow rules");
    }

    private void assertOrganizationCategory(boolean isReload) {
        assertEquals(7, organizationCategoryRepo.count(),
            isReload
                ? "Incorrect number of organizationCategory found after reload"
                : "Incorrect number of organizationCategory found");
    }

    private void assertWorkflowStep(boolean isReload) {
        assertEquals(4, workflowStepRepo.count(),
            isReload
                ? "Incorrect number of workflowStep found after reload"
                : "Incorrect number of workflowStep found");


        assertWorkflowStepHeritableProperties(new int[] { 3, 18 }, "Personal Information", isReload);
        assertWorkflowStepHeritableProperties(new int[] { 0, 2 }, "License Agreement", isReload);
        assertWorkflowStepHeritableProperties(new int[] { 4, 13 }, "Document Information", isReload);
        assertWorkflowStepHeritableProperties(new int[] { 2, 8 }, "File Upload", isReload);
    }

    private void assertWorkflowStepHeritableProperties(int[] expected, String name, boolean isReload) {
        WorkflowStep workflowStep = workflowStepRepo.findByName(name).get(0);
        assertEquals(expected[0], workflowStep.getOriginalNotes().size(),
            isReload
                ? String.format("%s workflow step has incorrect number of original notes after reload", name)
                : String.format("%s workflow step has incorrect number of original notes", name));
        assertEquals(expected[0], workflowStep.getAggregateNotes().size(),
            isReload
                ? String.format("%s workflow step has incorrect number of aggregate notes after reload", name)
                : String.format("%s workflow step has incorrect number of aggregate notes", name));
        assertEquals(expected[1], workflowStep.getOriginalFieldProfiles().size(),
            isReload
                ? String.format("%s workflow step has incorrect number of original field profiles after reload", name)
                : String.format("%s workflow step has incorrect number of original field profiles", name));
        assertEquals(expected[1], workflowStep.getAggregateFieldProfiles().size(),
            isReload
                ? String.format("%s workflow step has incorrect number of aggregate field profiles after reload", name)
                : String.format("%s workflow step has incorrect number of aggregate field profiles", name));
    }

    private void assertNote(boolean isReload) {
        assertEquals(9, noteRepo.count(),
            isReload
                ? "Incorrect number of note found after reload"
                : "Incorrect number of note found");
    }

    private void assertFieldProfile(boolean isReload) {
        assertEquals(41, fieldProfileRepo.count(),
            isReload
                ? "Incorrect number of fieldProfile found after reload"
                : "Incorrect number of fieldProfile found");
    }

    private void assertConfiguration(boolean isReload) {
        assertEquals(2, configurationRepo.count(),
            isReload
                ? "Incorrect number of configuration found after reload"
                : "Incorrect number of configuration found");
    }

    private void assertFieldPredicate(boolean isReload) {
        assertEquals(41, fieldPredicateRepo.count(),
            isReload
                ? "Incorrect number of fieldPredicate found after reload"
                : "Incorrect number of fieldPredicate found");
    }

    private void assertControlledVocabulary(boolean isReload) {
        assertEquals(14, controlledVocabularyRepo.count(),
            isReload
                ? "Incorrect number of controlledVocabulary found after reload"
                : "Incorrect number of controlledVocabulary found");
    }

    private void assertVocabulary(boolean isReload) {
        assertEquals(559, vocabularyRepo.count(),
            isReload
                ? "Incorrect number of vocabulary found after reload"
                : "Incorrect number of vocabulary found");
    }

    private void assertLanguage(boolean isReload) {
        assertEquals(1, languageRepo.count(),
            isReload
                ? "Incorrect number of language found after reload"
                : "Incorrect number of language found");
    }

    private void assertDegree(boolean isReload) {
        assertEquals(48, degreeRepo.count(),
            isReload
                ? "Incorrect number of degree found after reload"
                : "Incorrect number of degree found");
    }

    private void assertDegreeLevel(boolean isReload) {
        assertEquals(4, degreeLevelRepo.count(),
            isReload
                ? "Incorrect number of degreeLevel found after reload"
                : "Incorrect number of degreeLevel found");
    }

    private void assertGraduationMonth(boolean isReload) {
        assertEquals(3, graduationMonthRepo.count(),
            isReload
                ? "Incorrect number of graduationMonth found after reload"
                : "Incorrect number of graduationMonth found");
    }

    private void assertDocumentType(boolean isReload) {
        assertEquals(8, documentTypeRepo.count(),
            isReload
                ? "Incorrect number of documentType found after reload"
                : "Incorrect number of documentType found");
    }

    private void assertEmailWorkflowRule(boolean isReload) {
        assertEquals(2, emailWorkflowRuleRepo.count(),
            isReload
                ? "Incorrect number of emailWorkflowRule found after reload"
                : "Incorrect number of emailWorkflowRule found");
    }

    private void assertSubmissionStatus(boolean isReload) {
        assertEquals(12, submissionStatusRepo.count(),
            isReload
                ? "Incorrect number of submissionStatus found after reload"
                : "Incorrect number of submissionStatus found");

        SubmissionStatus inProgress = submissionStatusRepo.findByName("In Progress");
        SubmissionStatus submitted = submissionStatusRepo.findByName("Submitted");
        SubmissionStatus underReview = submissionStatusRepo.findByName("Under Review");
        SubmissionStatus needsCorrection = submissionStatusRepo.findByName("Needs Correction");
        SubmissionStatus correctionsReceived = submissionStatusRepo.findByName("Corrections Received");
        SubmissionStatus waitingOnRequirements = submissionStatusRepo.findByName("Waiting On Requirements");
        SubmissionStatus approved = submissionStatusRepo.findByName("Approved");
        SubmissionStatus pendingPublication = submissionStatusRepo.findByName("Pending Publication");
        SubmissionStatus published = submissionStatusRepo.findByName("Published");
        SubmissionStatus onHold = submissionStatusRepo.findByName("On Hold");
        SubmissionStatus withdrawn = submissionStatusRepo.findByName("Withdrawn");
        SubmissionStatus cancelled = submissionStatusRepo.findByName("Cancelled");

        assertSubmissionStatusState(SubmissionState.IN_PROGRESS, inProgress, isReload);
        assertSubmissionStatusState(SubmissionState.SUBMITTED, submitted, isReload);
        assertSubmissionStatusState(SubmissionState.UNDER_REVIEW, underReview, isReload);
        assertSubmissionStatusState(SubmissionState.NEEDS_CORRECTIONS, needsCorrection, isReload);
        assertSubmissionStatusState(SubmissionState.CORRECTIONS_RECIEVED, correctionsReceived, isReload);
        assertSubmissionStatusState(SubmissionState.WAITING_ON_REQUIREMENTS, waitingOnRequirements, isReload);
        assertSubmissionStatusState(SubmissionState.APPROVED, approved, isReload);
        assertSubmissionStatusState(SubmissionState.PENDING_PUBLICATION, pendingPublication, isReload);
        assertSubmissionStatusState(SubmissionState.PUBLISHED, published, isReload);
        assertSubmissionStatusState(SubmissionState.ON_HOLD, onHold, isReload);
        assertSubmissionStatusState(SubmissionState.WITHDRAWN, withdrawn, isReload);
        assertSubmissionStatusState(SubmissionState.CANCELED, cancelled, isReload);

        assertSubmissionStatusTransitions(new String[] { "Submitted", "On Hold", "Withdrawn", "Cancelled" }, inProgress, isReload);
        assertSubmissionStatusTransitions(new String[] { "Under Review" }, submitted, isReload);
        assertSubmissionStatusTransitions(new String[] { "Needs Correction", "Waiting On Requirements", "Approved" }, underReview, isReload);
        assertSubmissionStatusTransitions(new String[] { "Corrections Received" }, needsCorrection, isReload);
        assertSubmissionStatusTransitions(new String[] { "Under Review", "Waiting On Requirements", "Approved" }, correctionsReceived, isReload);
        assertSubmissionStatusTransitions(new String[] { "Under Review", "Approved" }, waitingOnRequirements, isReload);
        assertSubmissionStatusTransitions(new String[] { "Pending Publication" }, approved, isReload);
        assertSubmissionStatusTransitions(new String[] { "Published" }, pendingPublication, isReload);
        assertSubmissionStatusTransitions(new String[] { "Pending Publication" }, published, isReload);
        assertSubmissionStatusTransitions(new String[] { }, onHold, isReload);
        assertSubmissionStatusTransitions(new String[] { }, withdrawn, isReload);
        assertSubmissionStatusTransitions(new String[] { }, cancelled, isReload);

        assertSubmissionStatusState(new Boolean[] { false, false, false, false, false, null }, inProgress, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, submitted, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, underReview, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, true, true }, needsCorrection, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, correctionsReceived, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, waitingOnRequirements, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, approved, isReload);
        assertSubmissionStatusState(new Boolean[] { true, false, false, true, false, false }, pendingPublication, isReload);
        assertSubmissionStatusState(new Boolean[] { true, false, false, true, false, false }, published, isReload);
        assertSubmissionStatusState(new Boolean[] { false, false, false, true, false, true }, onHold, isReload);
        assertSubmissionStatusState(new Boolean[] { true, false, false, true, false, false }, withdrawn, isReload);
        assertSubmissionStatusState(new Boolean[] { true, false, true, true, false, false }, cancelled, isReload);
    }

    private void assertSubmissionStatusState(Boolean[] expected, SubmissionStatus actual, boolean isReload) {
        assertEquals(expected[0], actual.isArchived(),
            isReload
                ? String.format("%s submission status has incorrect isArchived flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isArchived flag", actual.getName()));
        assertEquals(expected[1], actual.isPublishable(),
            isReload
                ? String.format("%s submission status has incorrect isPublishable flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isPublishable flag", actual.getName()));
        assertEquals(expected[2], actual.isDeletable(),
            isReload
                ? String.format("%s submission status has incorrect isDeletable flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isDeletable flag", actual.getName()));
        assertEquals(expected[3], actual.isEditableByReviewer(),
            isReload
                ? String.format("%s submission status has incorrect isEditableByReviewer flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isEditableByReviewer flag", actual.getName()));
        assertEquals(expected[4], actual.isEditableByStudent(),
            isReload
                ? String.format("%s submission status has incorrect isEditableByStudent flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isEditableByStudent flag", actual.getName()));
        assertEquals(expected[5], actual.isActive(),
            isReload
                ? String.format("%s submission status has incorrect isActive flag after reload", actual.getName())
                : String.format("%s submission status has incorrect isActive flag", actual.getName()));
    }

    private void assertSubmissionStatusState(SubmissionState expected, SubmissionStatus actual, boolean isReload) {
        assertEquals(expected, actual.getSubmissionState(),
            isReload
                ? String.format("%s submission status has incorrect submission state after reload", actual.getName())
                : String.format("%s submission status has incorrect submission state", actual.getName()));
    }

    private void assertSubmissionStatusTransitions(String[] expected, SubmissionStatus actual, boolean isReload) {
        assertEquals(expected.length, actual.getTransitionSubmissionStatuses().size(),
                isReload
                    ? String.format("%s submission status has incorrect number of transition status after reload", actual.getName())
                    : String.format("%s submission status has incorrect number of transition status", actual.getName()));
        for (String name : expected) {
            assertTrue(actual.getTransitionSubmissionStatuses().stream().anyMatch(ts -> ts.getName().equals(name)),
                isReload
                    ? String.format("%s submission status is missing %s transition status after reload", actual.getName(), name)
                    : String.format("%s submission status is missing %s transition status", actual.getName(), name));
        }
    }

    private void assertSubmissionListColumn(boolean isReload) {
        assertEquals(63, submissionListColumnRepo.count(),
            isReload
                ? "Incorrect number of submissionListColumn found after reload"
                : "Incorrect number of submissionListColumn found");
    }

    private void assertAbstractPackager(boolean isReload) {
        assertEquals(6, abstractPackagerRepo.count(),
            isReload
                ? "Incorrect number of abstractPackager found after reload"
                : "Incorrect number of abstractPackager found");
    }

}
