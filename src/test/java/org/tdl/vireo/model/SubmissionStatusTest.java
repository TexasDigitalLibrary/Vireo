package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class SubmissionStatusTest extends AbstractModelCustomMethodTest<SubmissionStatus> {

    @InjectMocks
    private SubmissionStatus submissionStatus;

    @Test
    public void testAddSubmissionStatusWhenInArray() {
        List<SubmissionStatus> submissionStatuses = new ArrayList<>();
        SubmissionStatus submissionStatus1 = new SubmissionStatus();
        SubmissionStatus submissionStatus2 = new SubmissionStatus();

        submissionStatus1.setId(1L);
        submissionStatus2.setId(2L);

        submissionStatuses.add(submissionStatus1);
        submissionStatuses.add(submissionStatus2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "transitionSubmissionStatuses", submissionStatuses);

        submissionStatus.addTransitionSubmissionStatus(submissionStatus2);

        assertEquals(3, submissionStatuses.size(), "Submission Status is not added to the Submission Statuseses array.");
    }

    @Test
    public void testRemoveSubmissionStatus() {
        List<SubmissionStatus> submissionStatuses = new ArrayList<>();
        SubmissionStatus submissionStatus1 = new SubmissionStatus();
        SubmissionStatus submissionStatus2 = new SubmissionStatus();

        submissionStatus1.setId(1L);
        submissionStatus2.setId(2L);

        submissionStatuses.add(submissionStatus1);
        submissionStatuses.add(submissionStatus2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "transitionSubmissionStatuses", submissionStatuses);

        submissionStatus.removeTransitionSubmissionStatus(submissionStatus2);

        assertFalse(submissionStatuses.contains(submissionStatus2), "Submission Status is still in the Submission Statuses array.");
    }

    @Override
    protected SubmissionStatus getInstance() {
        return submissionStatus;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("isArchived", "isArchived", true),
            Arguments.of("isArchived", "isArchived", false),
            Arguments.of("isPublishable", "isPublishable", true),
            Arguments.of("isPublishable", "isPublishable", false),
            Arguments.of("isDeletable", "isDeletable", true),
            Arguments.of("isDeletable", "isDeletable", false),
            Arguments.of("isEditableByReviewer", "isEditableByReviewer", true),
            Arguments.of("isEditableByReviewer", "isEditableByReviewer", false),
            Arguments.of("isEditableByStudent", "isEditableByStudent", true),
            Arguments.of("isEditableByStudent", "isEditableByStudent", false),
            Arguments.of("isActive", "isActive", true),
            Arguments.of("isActive", "isActive", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("isArchived", "isArchived", true),
            Arguments.of("isArchived", "isArchived", false),
            Arguments.of("isPublishable", "isPublishable", true),
            Arguments.of("isPublishable", "isPublishable", false),
            Arguments.of("isDeletable", "isDeletable", true),
            Arguments.of("isDeletable", "isDeletable", false),
            Arguments.of("isEditableByReviewer", "isEditableByReviewer", true),
            Arguments.of("isEditableByReviewer", "isEditableByReviewer", false),
            Arguments.of("isEditableByStudent", "isEditableByStudent", true),
            Arguments.of("isEditableByStudent", "isEditableByStudent", false),
            Arguments.of("isActive", "isActive", true),
            Arguments.of("isActive", "isActive", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<SubmissionStatus> transitionSubmissionStatuses = new ArrayList<>();
        transitionSubmissionStatuses.add(new SubmissionStatus());

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("submissionState", SubmissionState.APPROVED),
            Arguments.of("submissionState", SubmissionState.CANCELED),
            Arguments.of("transitionSubmissionStatuses", transitionSubmissionStatuses)
        );
    }

}
