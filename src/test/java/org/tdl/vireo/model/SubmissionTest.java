package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class SubmissionTest extends AbstractModelCustomMethodTest<Submission> {

    @InjectMocks
    private Submission submission;

    @Test
    public void testClearApproveApplication() {
        ReflectionTestUtils.setField(submission, "approveApplicationDate", Calendar.getInstance());
        ReflectionTestUtils.setField(submission, "approveApplication", true);

        submission.clearApproveApplication();

        assertEquals(false, ReflectionTestUtils.getField(getInstance(), "approveApplication"), "Approve Application is not false.");
        assertNull(ReflectionTestUtils.getField(getInstance(), "approveApplicationDate"), "Approve Application Date is not null.");
    }

    @Test
    public void testClearApproveAdvisor() {
        ReflectionTestUtils.setField(submission, "approveAdvisorDate", Calendar.getInstance());
        ReflectionTestUtils.setField(submission, "approveAdvisor", true);

        submission.clearApproveAdvisor();

        assertEquals(false, ReflectionTestUtils.getField(getInstance(), "approveAdvisor"), "Approve Advisor is not false.");
        assertNull(ReflectionTestUtils.getField(getInstance(), "approveAdvisorDate"), "Approve Advisor Date is not null.");
    }

    @Test
    public void testClearApproveEmbargo() {
        ReflectionTestUtils.setField(submission, "approveEmbargoDate", Calendar.getInstance());
        ReflectionTestUtils.setField(submission, "approveEmbargo", true);

        submission.clearApproveEmbargo();

        assertEquals(false, ReflectionTestUtils.getField(getInstance(), "approveEmbargo"), "Approve Embargo is not false.");
        assertNull(ReflectionTestUtils.getField(getInstance(), "approveEmbargoDate"), "Approve Embargo Date is not null.");
    }

    @Test
    public void testGenerateAdvisorAccessHash() {
        ReflectionTestUtils.setField(getInstance(), "advisorAccessHash", "");

        ReflectionTestUtils.invokeGetterMethod(submission, "generateAdvisorAccessHash");

        String got = (String) ReflectionTestUtils.getField(getInstance(), "advisorAccessHash");

        assertNotNull(got, "Advisor Access Hash is null.");
        assertTrue(got.length() > 0, "Advisor Access Hash is an empty string.");
    }

    @Test
    public void testGetCommitteeContactEmail() {
        Set<FieldValue> fieldValues = new HashSet<>();
        List<String> contacts = new ArrayList<>();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();
        FieldValue fieldValue1 = new FieldValue();
        FieldValue fieldValue2 = new FieldValue();

        contacts.add("contact");
        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(1L);
        fieldPredicate1.setValue("dc.contributor.advisor");
        fieldPredicate2.setValue("not.advisor");
        fieldValue1.setId(1L);
        fieldValue2.setId(2L);
        fieldValue1.setContacts(contacts);
        fieldValue1.setValue("value1");
        fieldValue2.setValue("value2");
        fieldValue1.setFieldPredicate(fieldPredicate1);
        fieldValue2.setFieldPredicate(fieldPredicate2);
        fieldValues.add(fieldValue1);
        fieldValues.add(fieldValue2);

        ReflectionTestUtils.setField(submission, "fieldValues", fieldValues);

        assertNotNull(submission.getCommitteeContactEmail(), "Committee Contact E-mail is null.");
        assertEquals(contacts.get(0), submission.getCommitteeContactEmail(), "Committee Contact E-mail does not match.");
    }

    @Test
    public void testGetCommitteeContactEmailReturnsNull() {
        Set<FieldValue> fieldValues = new HashSet<>();
        List<String> contacts = new ArrayList<>();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();
        FieldValue fieldValue1 = new FieldValue();
        FieldValue fieldValue2 = new FieldValue();

        contacts.add("contact");
        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(1L);
        fieldPredicate1.setValue("not.advisor");
        fieldPredicate2.setValue("not.advisor");
        fieldValue1.setId(1L);
        fieldValue2.setId(2L);
        fieldValue1.setContacts(contacts);
        fieldValue1.setValue("value1");
        fieldValue2.setValue("value2");
        fieldValue1.setFieldPredicate(fieldPredicate1);
        fieldValue2.setFieldPredicate(fieldPredicate2);
        fieldValues.add(fieldValue1);
        fieldValues.add(fieldValue2);

        ReflectionTestUtils.setField(submission, "fieldValues", fieldValues);

        assertNull(submission.getCommitteeContactEmail(), "Committee Contact E-mail is not null.");
    }

    @Override
    protected Submission getInstance() {
        return submission;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getApproveEmbargo", "approveEmbargo", true),
            Arguments.of("getApproveEmbargo", "approveEmbargo", false),
            Arguments.of("isApproveApplication", "approveApplication", true),
            Arguments.of("isApproveApplication", "approveApplication", false),
            Arguments.of("getApproveAdvisor", "approveAdvisor", true),
            Arguments.of("getApproveAdvisor", "approveAdvisor", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setApproveEmbargo", "approveEmbargo", true),
            Arguments.of("setApproveEmbargo", "approveEmbargo", false),
            Arguments.of("setApproveApplication", "approveApplication", true),
            Arguments.of("setApproveApplication", "approveApplication", false),
            Arguments.of("setApproveAdvisor", "approveAdvisor", true),
            Arguments.of("setApproveAdvisor", "approveAdvisor", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        Set<FieldValue> fieldValues = new HashSet<>();
        List<SubmissionWorkflowStep> submissionWorkflowSteps = new ArrayList<>();
        Set<CustomActionValue> customActionValues = new HashSet<>();
        Set<ActionLog> actionLogs = new HashSet<>();

        return Stream.of(
            Arguments.of("submitter", new User()),
            Arguments.of("assignee", new User()),
            Arguments.of("submissionStatus", new SubmissionStatus()),
            Arguments.of("organization", new Organization()),
            Arguments.of("fieldValues", fieldValues),
            Arguments.of("submissionWorkflowSteps", submissionWorkflowSteps),
            Arguments.of("approveEmbargoDate", Calendar.getInstance()),
            Arguments.of("approveApplicationDate", Calendar.getInstance()),
            Arguments.of("submissionDate", Calendar.getInstance()),
            Arguments.of("approveAdvisorDate", Calendar.getInstance()),
            Arguments.of("customActionValues", customActionValues),
            Arguments.of("actionLogs", actionLogs),
            Arguments.of("reviewerNotes", "value"),
            Arguments.of("advisorAccessHash", "value"),
            Arguments.of("advisorReviewURL", "value"),
            Arguments.of("depositURL", "value")
        );
    }

}
