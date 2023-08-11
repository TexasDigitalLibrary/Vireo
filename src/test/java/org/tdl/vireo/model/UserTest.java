package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTest extends AbstractModelTest<User> {

    @InjectMocks
    private User user;

    @Test
    public void testUserInstantiation() {
        user.setEmail("email");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setRole(Role.ROLE_ADMIN);

        User newUser = new User(user);

        assertEquals(newUser.getEmail(), user.getEmail(), "E-mail does not match.");
        assertEquals(newUser.getFirstName(), user.getFirstName(), "First Name does not match.");
        assertEquals(newUser.getLastName(), user.getLastName(), "Last Name does not match.");
        assertEquals(newUser.getRole(), user.getRole(), "Role does not match.");
    }

    /**
     * The getSettings() does not work via getParameterStream() and is manually tested here.
     */
    @Test
    public void testGetSettings() {
        Map<String, String> settings = new HashMap<>();

        ReflectionTestUtils.setField(user, "settings", settings);

        assertEquals(settings, user.getSettings(), GETTER_MESSAGE + "settings.");
    }

    /**
     * The setSettings() does not work via setParameterStream() and is manually tested here.
     */
    @Test
    public void testSetSettings() {
        Map<String, String> settings = new HashMap<>();

        user.setSettings(settings);

        assertEquals(settings, ReflectionTestUtils.getField(getInstance(), "settings"), SETTER_MESSAGE + "settings.");
    }

    @Test
    public void testPutSetting() {
        Map<String, String> settings = new HashMap<>();
        String key = "key";
        String value = "value";

        ReflectionTestUtils.setField(user, "settings", settings);

        user.putSetting(key, value);

        assertTrue(settings.containsKey(key), "Could not find key in the settings.");
        assertEquals(settings.get(key), value, "The value for the key does not match the expected value.");
    }

    @Test
    public void testRemoveShibbolethAffiliation() {
        TreeSet<String> shibbolethAffiliations = new TreeSet<>();
        String shibbolethAffiliation = "affiliation";
        shibbolethAffiliations.add(shibbolethAffiliation);

        ReflectionTestUtils.setField(user, "shibbolethAffiliations", shibbolethAffiliations);

        user.removeShibbolethAffiliation(shibbolethAffiliation);

        assertFalse(shibbolethAffiliations.contains(shibbolethAffiliation), "The Shibboleth Affiliation is not removed.");
    }

    @Test
    public void testAddSubmissionViewColumn() {
        List<SubmissionListColumn> submissionViewColumns = new ArrayList<>();
        SubmissionListColumn submissionListColumn = new SubmissionListColumn();

        submissionListColumn.setId(1L);

        ReflectionTestUtils.setField(user, "submissionViewColumns", submissionViewColumns);

        user.addSubmissionViewColumn(submissionListColumn);

        assertTrue(submissionViewColumns.contains(submissionListColumn), "The Submission View Column is not added.");
    }

    @Test
    public void testRemoveSubmissionViewColumn() {
        List<SubmissionListColumn> submissionViewColumns = new ArrayList<>();
        SubmissionListColumn submissionListColumn = new SubmissionListColumn();

        submissionListColumn.setId(1L);
        submissionViewColumns.add(submissionListColumn);

        ReflectionTestUtils.setField(user, "submissionViewColumns", submissionViewColumns);

        user.removeSubmissionViewColumn(submissionListColumn);

        assertFalse(submissionViewColumns.contains(submissionListColumn), "The Submission View Column is not removed.");
    }

    @Test
    public void testAddSavedFilter() {
        List<NamedSearchFilterGroup> savedFilters = new ArrayList<>();
        NamedSearchFilterGroup namedSearchFilterGroup = new NamedSearchFilterGroup();

        namedSearchFilterGroup.setId(1L);

        ReflectionTestUtils.setField(user, "savedFilters", savedFilters);

        user.addSavedFilter(namedSearchFilterGroup);

        assertTrue(savedFilters.contains(namedSearchFilterGroup), "The Saved Filter is not added.");
    }

    @Test
    public void testRemoveSavedFilter() {
        List<NamedSearchFilterGroup> savedFilters = new ArrayList<>();
        NamedSearchFilterGroup namedSearchFilterGroup = new NamedSearchFilterGroup();

        namedSearchFilterGroup.setId(1L);
        savedFilters.add(namedSearchFilterGroup);

        ReflectionTestUtils.setField(user, "savedFilters", savedFilters);

        user.removeSavedFilter(namedSearchFilterGroup);

        assertFalse(savedFilters.contains(namedSearchFilterGroup), "The Saved Filter is not removed.");
    }

    @Test
    public void testLoadActiveFilter() {
        NamedSearchFilterGroup activeFilter = new NamedSearchFilterGroup();
        NamedSearchFilterGroup namedSearchFilterGroup = new NamedSearchFilterGroup();
        List<SubmissionListColumn> savedColumns = new ArrayList<SubmissionListColumn>();
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        SubmissionListColumn submissionListColumn = new SubmissionListColumn();
        NamedSearchFilter namedSearchFilter = new NamedSearchFilter();

        submissionListColumn.setId(1L);
        savedColumns.add(submissionListColumn);

        namedSearchFilter.setId(1L);
        namedSearchFilters.add(namedSearchFilter);

        activeFilter.setId(1L);
        activeFilter.setPublicFlag(false);
        activeFilter.setColumnsFlag(false);

        namedSearchFilterGroup.setId(2L);
        namedSearchFilterGroup.setSavedColumns(savedColumns);
        namedSearchFilterGroup.setNamedSearchFilters(namedSearchFilters);
        namedSearchFilterGroup.setPublicFlag(true);
        namedSearchFilterGroup.setColumnsFlag(true);

        ReflectionTestUtils.setField(user, "activeFilter", activeFilter);

        user.loadActiveFilter(namedSearchFilterGroup);

        assertEquals(activeFilter.getSavedColumns(), savedColumns, "The Saved Columns is not added.");
        assertEquals(activeFilter.getNamedSearchFilters(), namedSearchFilters, "The Named Search Filters is not added.");
        assertEquals(activeFilter.getPublicFlag(), namedSearchFilterGroup.getPublicFlag(), "The Public Flag is not set correctly.");
        assertEquals(activeFilter.getColumnsFlag(), namedSearchFilterGroup.getColumnsFlag(), "The Columns Flag is not set correctly.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAuthorities() {
        Role role = Role.ROLE_MANAGER;
        user.setRole(role);

        List<GrantedAuthority> authority = (List<GrantedAuthority>) user.getAuthorities();

        assertEquals(authority.get(0).getAuthority(), role.toString(), "The expected role is not set as the authority.");
    }

    @Override
    protected User getInstance() {
        return user;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        Set<String> shibbolethAffiliations = new HashSet<>();
        List<SubmissionListColumn> submissionViewColumns = new ArrayList<>();
        List<SubmissionListColumn> filterColumns = new ArrayList<>();
        List<NamedSearchFilterGroup> savedFilters = new ArrayList<>();
        shibbolethAffiliations.add("value");

        return Stream.of(
            Arguments.of("netid", "value"),
            Arguments.of("email", "value"),
            Arguments.of("password", "value"),
            Arguments.of("firstName", "value"),
            Arguments.of("lastName", "value"),
            Arguments.of("middleName", "value"),
            Arguments.of("name", "value"),
            Arguments.of("birthYear", 123),
            Arguments.of("shibbolethAffiliations", shibbolethAffiliations),
            Arguments.of("currentContactInfo", new ContactInfo()),
            Arguments.of("permanentContactInfo", new ContactInfo()),
            Arguments.of("role", Role.ROLE_ADMIN),
            Arguments.of("role", Role.ROLE_ANONYMOUS),
            Arguments.of("orcid", "value"),
            Arguments.of("pageSize", 123),
            Arguments.of("submissionViewColumns", submissionViewColumns),
            Arguments.of("filterColumns", filterColumns),
            Arguments.of("activeFilter", new NamedSearchFilterGroup()),
            Arguments.of("savedFilters", savedFilters)
        );
    }

}
