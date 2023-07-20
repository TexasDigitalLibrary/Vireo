package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

@ActiveProfiles("test")
public class SubmissionListControllerTest extends AbstractControllerTest {

    @Mock
    private DefaultSubmissionListColumnService defaultSubmissionListColumnService;

    @Mock
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Mock
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @Mock
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private SubmissionListController submissionlistController;

    private NamedSearchFilter namedSearchFilter1;

    private NamedSearchFilterGroup namedSearchFilterGroup1;

    private SubmissionListColumn submissionListColumn1;
    private SubmissionListColumn submissionListColumn2;

    private User user1;

    private List<SubmissionListColumn> submissionListColumns1;
    private List<SubmissionListColumn> submissionListColumns2;

    private Set<NamedSearchFilter> namedSearchFilters1;

    @BeforeEach
    public void setup() {
        namedSearchFilter1 = new NamedSearchFilter();
        namedSearchFilterGroup1 = new NamedSearchFilterGroup();
        submissionListColumn1 = new SubmissionListColumn("title1", Sort.NONE);
        submissionListColumn2 = new SubmissionListColumn("title2", Sort.NONE);
        user1 = new User("email1", "firstname1", "lastname1", Role.ROLE_ADMIN);

        namedSearchFilter1.setId(1L);
        namedSearchFilterGroup1.setId(1L);
        submissionListColumn1.setId(1L);
        submissionListColumn2.setId(2L);
        user1.setId(1L);

        namedSearchFilters1 = new HashSet<>(Arrays.asList(namedSearchFilter1));
        submissionListColumns1 = new ArrayList<>(Arrays.asList(submissionListColumn1));
        submissionListColumns2 = new ArrayList<>(Arrays.asList(submissionListColumn2));

        namedSearchFilterGroup1.setNamedSearchFilters(namedSearchFilters1);
    }

    @Test
    public void testGetSubmissionViewColumns() {
        when(submissionListColumnRepo.findAll()).thenReturn(submissionListColumns1);

        ApiResponse response = submissionlistController.getSubmissionViewColumns();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionListColumn>");
        assertEquals(submissionListColumns1, got);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testGetSubmissionViewColumnsByUser(boolean withColumns) {
        ReflectionTestUtils.setField(namedSearchFilterGroup1, "columnsFlag", withColumns);
        ReflectionTestUtils.setField(user1, "activeFilter", namedSearchFilterGroup1);

        if (withColumns) {
            ReflectionTestUtils.setField(namedSearchFilterGroup1, "savedColumns", submissionListColumns1);
        } else {
            ReflectionTestUtils.setField(user1, "submissionViewColumns", submissionListColumns1);
        }

        ApiResponse response = submissionlistController.getSubmissionViewColumnsByUser(user1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionListColumn>");
        assertEquals(submissionListColumns1, got);
    }

    @Test
    public void testGetFilterColumnsByUser() {
        ReflectionTestUtils.setField(user1, "filterColumns", submissionListColumns1);

        ApiResponse response = submissionlistController.getFilterColumnsByUser(user1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionListColumn>");
        assertEquals(submissionListColumns1, got);
    }

    @Test
    public void testGetSubmissionViewPageSizeByUser() {
        Integer size = 1;
        ReflectionTestUtils.setField(user1, "pageSize", size);

        ApiResponse response = submissionlistController.getSubmissionViewPageSizeByUser(user1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Integer got = (Integer) response.getPayload().get("Integer");
        assertEquals(size, got);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testUpdateUserSubmissionViewColumns(boolean withColumns) {
        Integer size = 1;

        ReflectionTestUtils.setField(namedSearchFilterGroup1, "columnsFlag", withColumns);
        ReflectionTestUtils.setField(user1, "pageSize", 0);
        ReflectionTestUtils.setField(user1, "submissionViewColumns", new ArrayList<SubmissionListColumn>());
        ReflectionTestUtils.setField(user1, "activeFilter", namedSearchFilterGroup1);

        when(userRepo.update(any(User.class))).thenReturn(user1);

        if (withColumns) {
            when(namedSearchFilterGroupRepo.create(any(User.class))).thenReturn(namedSearchFilterGroup1);
            when(namedSearchFilterRepo.clone(any(NamedSearchFilter.class))).thenReturn(namedSearchFilter1);
        }

        ApiResponse response = submissionlistController.updateUserSubmissionViewColumns(user1, size, submissionListColumns1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionListColumn>");
        assertEquals(submissionListColumns1, got);
        assertEquals(submissionListColumns1, user1.getSubmissionViewColumns());
        assertEquals(size, user1.getPageSize());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testResetUserSubmissionViewColumns(boolean withColumns) {
        List<SubmissionListColumn> defaultColumns = new ArrayList<>();

        ReflectionTestUtils.setField(namedSearchFilterGroup1, "columnsFlag", withColumns);
        ReflectionTestUtils.setField(user1, "pageSize", 0);
        ReflectionTestUtils.setField(user1, "submissionViewColumns", submissionListColumns1);
        ReflectionTestUtils.setField(user1, "activeFilter", namedSearchFilterGroup1);

        when(defaultSubmissionListColumnService.getDefaultSubmissionListColumns()).thenReturn(defaultColumns);
        when(userRepo.update(any(User.class))).thenReturn(user1);

        if (withColumns) {
            when(namedSearchFilterGroupRepo.create(any(User.class))).thenReturn(namedSearchFilterGroup1);
            when(namedSearchFilterRepo.clone(any(NamedSearchFilter.class))).thenReturn(namedSearchFilter1);
        }

        ApiResponse response = submissionlistController.resetUserSubmissionViewColumns(user1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList");

        assertEquals(defaultColumns, got);
        assertEquals(defaultColumns, user1.getSubmissionViewColumns());
        assertEquals(10, user1.getPageSize());
    }

    @Test
    public void testUpdateUserFilterColumns() {
        when(userRepo.update(any(User.class))).thenReturn(user1);

        ReflectionTestUtils.setField(user1, "filterColumns", submissionListColumns1);

        ApiResponse response = submissionlistController.updateUserFilterColumns(user1, submissionListColumns2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionListColumn>");
        assertEquals(submissionListColumns2, got);
        assertEquals(submissionListColumns2, user1.getFilterColumns());
    }

    @ParameterizedTest
    @MethodSource("provideSortEnums")
    public void testUpdateSort(Sort sort, Integer sortOrder) {
        ReflectionTestUtils.setField(submissionListColumn1, "sort", sort);
        ReflectionTestUtils.setField(submissionListColumn1, "sortOrder", sortOrder);
        ReflectionTestUtils.setField(namedSearchFilterGroup1, "sortColumnTitle", "some title");
        ReflectionTestUtils.setField(namedSearchFilterGroup1, "sortDirection", sort == Sort.NONE ? Sort.ASC : Sort.NONE);
        ReflectionTestUtils.setField(user1, "activeFilter", namedSearchFilterGroup1);

        when(namedSearchFilterGroupRepo.update(any(NamedSearchFilterGroup.class))).thenReturn(namedSearchFilterGroup1);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = submissionlistController.updateSort(user1, submissionListColumns1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        NamedSearchFilterGroup got = (NamedSearchFilterGroup) response.getPayload().get("NamedSearchFilterGroup");
        assertEquals(namedSearchFilterGroup1, got);

        if (sortOrder == 0) {
            assertNotEquals(submissionListColumn1.getTitle(), namedSearchFilterGroup1.getSortColumnTitle());
            assertNotEquals(submissionListColumn1.getSort(), namedSearchFilterGroup1.getSortDirection());
        } else {
            assertEquals(submissionListColumn1.getTitle(), namedSearchFilterGroup1.getSortColumnTitle());
            assertEquals(submissionListColumn1.getSort(), namedSearchFilterGroup1.getSortDirection());
        }
    }

    private static Stream<Arguments> provideSortEnums() {
        return Stream.of(
            Arguments.of(Sort.NONE, 0),
            Arguments.of(Sort.NONE, 1),
            Arguments.of(Sort.ASC, 0),
            Arguments.of(Sort.ASC, 1),
            Arguments.of(Sort.DESC, 0),
            Arguments.of(Sort.DESC, 1)
        );
    }

}
