package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.request.FilteredPageRequest;
import org.tdl.vireo.model.response.Views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ActiveProfiles(value = { "test", "isolated-test" })
public class UserControllerTest extends AbstractControllerTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    private Credentials credentials1;
    private User user1;
    private User user2;

    private List<User> users;
    private Page<User> page1;
    private Map<String, String> settings1;
    private Map<String, String> settings2;

    private Pageable pageable;

    @BeforeEach
    public void setup() {
        credentials1 = new Credentials();
        user1 = new User("email1", "firstname1", "lastname1", Role.ROLE_ADMIN);
        user2 = new User("email2", "firstname2", "lastname2", Role.ROLE_MANAGER);

        user1.setId(1L);
        user2.setId(2L);

        users = new ArrayList<>(Arrays.asList(user1));
        page1 = new PageImpl<>(users);
        settings1 = new HashMap<>();
        settings2 = new HashMap<>();

        settings1.put("key1", "value1");
        settings2.put("key2", "value2");

        user1.setSettings(settings1);

        pageable = PageRequest.of(0, users.size());
    }

    @Test
    public void testCredentials() {
        ApiResponse response = userController.credentials(credentials1, user1);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Credentials got = (Credentials) response.getPayload().get("Credentials");

        assertEquals(credentials1, got);
    }

    @Test
    public void testCredentialsCarriesPasswordEligibleTrue() {
        user1.setPassword("encodedpassword");
        user1.setNetid(null);

        ApiResponse response = userController.credentials(credentials1, user1);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Credentials got = (Credentials) response.getPayload().get("Credentials");

        // The map is Map<String, String>, so the value is the String "true", not a JSON boolean.
        assertEquals("true", got.getAllCredentials().get("passwordEligible"));
    }

    @Test
    public void testCredentialsCarriesPasswordEligibleFalse() {
        user1.setPassword("encodedpassword");
        user1.setNetid("netid1");

        ApiResponse response = userController.credentials(credentials1, user1);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Credentials got = (Credentials) response.getPayload().get("Credentials");

        assertEquals("false", got.getAllCredentials().get("passwordEligible"));
    }

    @Test
    public void testCredentialsPasswordEligibleIsAlwaysAStringValue() {
        user1.setPassword("   ");
        user1.setNetid(null);

        ApiResponse response = userController.credentials(credentials1, user1);

        Credentials got = (Credentials) response.getPayload().get("Credentials");
        Object value = got.getAllCredentials().get("passwordEligible");

        assertTrue(value instanceof String, "passwordEligible must ride in the map as a String");
        assertEquals("false", value, "a whitespace-only password is ineligible");
    }

    @Test
    public void testCredentialsGuardsNullAllCredentials() {
        // The no-arg Credentials constructor leaves allCredentials null, so the handler must create the map.
        credentials1.setAllCredentials(null);

        user1.setPassword("encodedpassword");
        user1.setNetid(null);

        ApiResponse response = userController.credentials(credentials1, user1);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Credentials got = (Credentials) response.getPayload().get("Credentials");

        assertNotNull(got.getAllCredentials(), "the handler must set a map when none is present");
        assertEquals("true", got.getAllCredentials().get("passwordEligible"));
    }

    @Test
    public void testPasswordEligibleSerializesWithNoActiveView() throws Exception {
        // Un-annotated properties serialize only with no active view (default-view-inclusion is off).
        user1.setPassword("encodedpassword");
        user1.setNetid(null);

        JsonNode payload = viewAwareMapper().readTree(viewAwareMapper().writeValueAsString(user1));

        assertTrue(payload.has("passwordEligible"), "passwordEligible must serialize with no view active");
        assertTrue(payload.get("passwordEligible").asBoolean(), "an eligible Account serializes true");
    }

    @Test
    public void testPasswordEligibleIsAbsentUnderAnActiveView() throws Exception {
        user1.setPassword("encodedpassword");
        user1.setNetid(null);

        ObjectMapper mapper = viewAwareMapper();

        for (Class<?> view : Arrays.asList(Views.SubmissionList.class, Views.Partial.class, Views.SubmissionIndividual.class, Views.SubmissionIndividualActionLogs.class)) {
            JsonNode payload = mapper.readTree(mapper.writerWithView(view).writeValueAsString(user1));

            assertFalse(payload.has("passwordEligible"), "passwordEligible must be absent under the active view " + view.getSimpleName());
        }
    }

    @Test
    public void testPasswordEligibleSerializesFalseForAnIneligibleAccount() throws Exception {
        user1.setPassword("encodedpassword");
        user1.setNetid("netid1");

        ObjectMapper mapper = viewAwareMapper();
        JsonNode payload = mapper.readTree(mapper.writeValueAsString(user1));

        assertTrue(payload.has("passwordEligible"));
        assertFalse(payload.get("passwordEligible").asBoolean(), "an Account carrying a netid serializes false");
    }

    /** Mapper matching application.yml (default-view-inclusion off); the inherited objectMapper is a shared spy. */
    private ObjectMapper viewAwareMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        return mapper;
    }

    @Test
    public void testAllUsers() {
        when(userRepo.findAll()).thenReturn(users);

        ApiResponse response = userController.allUsers();

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPage() {
        when(userRepo.findAll((Specification<User>) argThat(argument -> argument instanceof Specification), any(Pageable.class))).thenReturn(page1);

        FilteredPageRequest fpr = new FilteredPageRequest();
        fpr.setPageNumber(0);
        fpr.setPageSize(2);
        ApiResponse response = userController.page(fpr);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(userRepo).findAll((Specification<User>) argThat(argument -> argument instanceof Specification), any(Pageable.class));
    }

    @Test
    public void testAllAssignableUsersEmptyName() {
        when(userRepo.findAllByRoleIn(anyList(), any(Sort.class))).thenReturn(users);

        ApiResponse response = userController.allAssignableUsers(0, "", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllAssignableUsers() {
        when(userRepo.findAllByRoleInAndNameContainsIgnoreCase(anyList(), anyString(), any(Sort.class))).thenReturn(users);

        ApiResponse response = userController.allAssignableUsers(0, "name", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllAssignableUsersPaginatedEmptyName() {
        when(userRepo.findAllByRoleIn(anyList(), any(Pageable.class))).thenReturn(users);

        ApiResponse response = userController.allAssignableUsers(1, "", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllAssignableUsersPaginated() {
        when(userRepo.findAllByRoleInAndNameContainsIgnoreCase(anyList(), anyString(), any(Pageable.class))).thenReturn(users);

        ApiResponse response = userController.allAssignableUsers(1, "name", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testCountAssignableUsersEmptyName() {
        Long count = 1L;

        when(userRepo.countByRoleIn(anyList())).thenReturn(count);

        ApiResponse response = userController.countAssignableUsers("");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Long got = (Long) response.getPayload().get("Long");

        assertEquals(count, got);
    }

    @Test
    public void testCountAssignableUsers() {
        Long count = 1L;

        when(userRepo.countByRoleInAndNameContainsIgnoreCase(anyList(), anyString())).thenReturn(count);

        ApiResponse response = userController.countAssignableUsers("name");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Long got = (Long) response.getPayload().get("Long");

        assertEquals(count, got);
    }

    @Test
    public void testAllUnassignableUsersEmptyName() {
        when(userRepo.findAllByRoleIn(anyList(), any(Sort.class))).thenReturn(users);

        ApiResponse response = userController.allUnassignableUsers(0, "", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllUnassignableUsers() {
        when(userRepo.findAllByRoleInAndNameContainsIgnoreCase(anyList(), anyString(), any(Sort.class))).thenReturn(users);

        ApiResponse response = userController.allUnassignableUsers(0, "name", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllUnassignablePaginatedEmptyName() {
        when(userRepo.findAllByRoleIn(anyList(), any(Pageable.class))).thenReturn(users);

        ApiResponse response = userController.allUnassignableUsers(1, "", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testAllUnassignablePaginated() {
        when(userRepo.findAllByRoleInAndNameContainsIgnoreCase(anyList(), anyString(), any(Pageable.class))).thenReturn(users);

        ApiResponse response = userController.allUnassignableUsers(1, "name", pageable);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<User>");

        assertEquals(users, got);
    }

    @Test
    public void testCountUnassignableUsersEmptyName() {
        Long count = 1L;

        when(userRepo.countByRoleIn(anyList())).thenReturn(count);

        ApiResponse response = userController.countUnassignableUsers("");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Long got = (Long) response.getPayload().get("Long");

        assertEquals(count, got);
    }

    @Test
    public void testCountUnassignableUsers() {
        Long count = 1L;

        when(userRepo.countByRoleInAndNameContainsIgnoreCase(anyList(), anyString())).thenReturn(count);

        ApiResponse response = userController.countUnassignableUsers("name");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Long got = (Long) response.getPayload().get("Long");

        assertEquals(count, got);
    }

    @Test
    public void testUpdate() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user1));
        when(userRepo.update(any(User.class))).thenReturn(user1);

        ApiResponse response = userController.update(user2);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        User got = (User) response.getPayload().get("User");

        assertEquals(user1, got);
    }

    @Test
    public void testGetSettings() {
        ApiResponse response = userController.getSettings(user1);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Map<?, ?> got = (HashMap<?, ?>) response.getPayload().get("HashMap");

        assertEquals(settings1, got);
    }

    @Test
    public void testUpdateSettings() {
        when(userRepo.update(any(User.class))).thenReturn(user1);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        // Warning: The function is named "updateSetting" rather than "updateSettings".
        ApiResponse response = userController.updateSetting(user1, settings2);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
