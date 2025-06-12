package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.exception.BatchExportException;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.utility.PackagerUtility;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ActiveProfiles(value = { "test", "isolated-test" })
public class SubmissionControllerTest extends AbstractControllerTest {

    private static final String TEST_USER_1_EMAIL = "User 1 email";
    private static final String TEST_USER_1_FIRST_NAME = "User 1 first name";
    private static final String TEST_USER_1_LAST_NAME = "User 1 last name";
    private static final String TEST_HASH = "97a74588d324c59d8d8fbdd5187292d7";
    private static final Role TEST_USER_1_ROLE = Role.ROLE_ADMIN;

    private static final User TEST_USER_1 = new User(TEST_USER_1_EMAIL, TEST_USER_1_FIRST_NAME, TEST_USER_1_LAST_NAME, TEST_USER_1_ROLE);

    private static final Calendar TEST_CALENDAR_1 = GregorianCalendar.getInstance();
    private static final Calendar TEST_CALENDAR_2 = GregorianCalendar.getInstance();
    private static final Calendar TEST_CALENDAR_3 = GregorianCalendar.getInstance();

    private static final SubmissionStatus TEST_SUBMISSION_STATUS_1 = new SubmissionStatus("In Progress", false, false, false, true, true, true, SubmissionState.IN_PROGRESS);
    private static final SubmissionStatus TEST_SUBMISSION_STATUS_2 = new SubmissionStatus("Needs Corrections", false, false, false, true, true, true, SubmissionState.NEEDS_CORRECTIONS);
    private static final SubmissionStatus TEST_SUBMISSION_STATUS_3 = new SubmissionStatus("Approved", false, false, false, true, true, true, SubmissionState.APPROVED);

    private static final ActionLog TEST_ACTION_LOG_1 = new ActionLog(Action.UNDETERMINED, TEST_SUBMISSION_STATUS_1, TEST_CALENDAR_1, "Action log entrty 1", false);
    private static final ActionLog TEST_ACTION_LOG_2 = new ActionLog(Action.UNDETERMINED, TEST_SUBMISSION_STATUS_2, TEST_CALENDAR_2, "Action log entrty 2", true);
    private static final ActionLog TEST_ACTION_LOG_3 = new ActionLog(Action.UNDETERMINED, TEST_SUBMISSION_STATUS_3, TEST_CALENDAR_3, "Action log entrty 3", false);

    private static final List<ActionLog> TEST_ACTION_LOG_LIST = new ArrayList<>(Arrays.asList(TEST_ACTION_LOG_1, TEST_ACTION_LOG_2, TEST_ACTION_LOG_3));
    private static final List<ActionLog> TEST_ACTION_LOG_LIST_PAGE_ASSIGNED = new ArrayList<>(Arrays.asList(TEST_ACTION_LOG_1));

    private static final Page<ActionLog> TEST_ACTION_LOG_PAGE = new PageImpl<>(TEST_ACTION_LOG_LIST);

    @Mock
    private ActionLogRepo actionLogRepo;

    @Mock
    private NamedSearchFilterGroup activeFilter;

    @Mock
    private PackagerUtility packagerUtility;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @InjectMocks
    private SubmissionController submissionController;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void testGetActionLogs() {
        when(actionLogRepo.getAllActionLogs(any(Long.class), any(), any(Pageable.class))).thenReturn(TEST_ACTION_LOG_PAGE);

        Pageable pageable = PageRequest.of(0, TEST_ACTION_LOG_LIST_PAGE_ASSIGNED.size());
        ApiResponse response = submissionController.getActionLogs(TEST_USER_1, 1L, pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        assertEquals(pageable.getPageSize(), response.getPayload().size());
    }

    @Test
    public void testGetActionLogsForAdvisement() {
        when(actionLogRepo.getAllActionLogs(any(String.class), any(), any(Pageable.class))).thenReturn(TEST_ACTION_LOG_PAGE);

        Pageable pageable = PageRequest.of(0, TEST_ACTION_LOG_LIST_PAGE_ASSIGNED.size());
        ApiResponse response = submissionController.getActionLogsForAdvisement(TEST_HASH, pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        assertEquals(pageable.getPageSize(), response.getPayload().size());
    }

    @Test
    void testBatchExportErrorMessage() {
        TEST_USER_1.setActiveFilter(activeFilter);
        when(packagerUtility.getPackager(anyString())).thenReturn(null);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        assertThrows(BatchExportException.class, () -> {
            submissionController.batchExport(mockResponse, TEST_USER_1, "INVALID-PACKAGER-NAME");
        });
    }
}
