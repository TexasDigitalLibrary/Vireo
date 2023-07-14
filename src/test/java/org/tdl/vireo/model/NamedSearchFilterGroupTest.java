package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class NamedSearchFilterGroupTest extends AbstractModelCustomMethodTest<NamedSearchFilterGroup> {

    @InjectMocks
    private NamedSearchFilterGroup namedSearchFilterGroup;

    @Test
    public void testAddSavedColumnWhenNotInArray() {
        List<SubmissionListColumn> savedColumns = new ArrayList<>();
        SubmissionListColumn submissionListColumn1 = new SubmissionListColumn();
        SubmissionListColumn submissionListColumn2 = new SubmissionListColumn();

        submissionListColumn1.setId(1L);
        submissionListColumn2.setId(2L);

        savedColumns.add(submissionListColumn1);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "savedColumns", savedColumns);

        namedSearchFilterGroup.addSavedColumn(submissionListColumn2);

        assertTrue(savedColumns.contains(submissionListColumn2), "Submission List Column is not added to the Saved Columns array.");
    }

    @Test
    public void testAddSavedColumnWhenInArray() {
        List<SubmissionListColumn> savedColumns = new ArrayList<>();
        SubmissionListColumn submissionListColumn1 = new SubmissionListColumn();
        SubmissionListColumn submissionListColumn2 = new SubmissionListColumn();

        submissionListColumn1.setId(1L);
        submissionListColumn2.setId(2L);

        savedColumns.add(submissionListColumn1);
        savedColumns.add(submissionListColumn2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "savedColumns", savedColumns);

        namedSearchFilterGroup.addSavedColumn(submissionListColumn2);

        assertEquals(2, savedColumns.size(), "Submission List Column is added to the Saved Columns array multiple times.");
    }

    @Test
    public void testRemoveSavedColumn() {
        List<SubmissionListColumn> savedColumns = new ArrayList<>();
        SubmissionListColumn submissionListColumn1 = new SubmissionListColumn();
        SubmissionListColumn submissionListColumn2 = new SubmissionListColumn();

        submissionListColumn1.setId(1L);
        submissionListColumn2.setId(2L);

        savedColumns.add(submissionListColumn1);
        savedColumns.add(submissionListColumn2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "savedColumns", savedColumns);

        namedSearchFilterGroup.removeSavedColumn(submissionListColumn2);

        assertFalse(savedColumns.contains(submissionListColumn2), "Submission List Column is still in the Saved Columns array.");
    }

    @Test
    public void testAddNamedSearchFilterWhenNotInArray() {
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        NamedSearchFilter filterCriterion1 = new NamedSearchFilter();
        NamedSearchFilter filterCriterion2 = new NamedSearchFilter();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);

        namedSearchFilters.add(filterCriterion1);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "namedSearchFilters", namedSearchFilters);

        namedSearchFilterGroup.addFilterCriterion(filterCriterion2);

        assertTrue(namedSearchFilters.contains(filterCriterion2), "Filter Criterion is not added to the Named Search Filters array.");
    }

    @Test
    public void testAddNamedSearchFilterWhenInArray() {
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        NamedSearchFilter filterCriterion1 = new NamedSearchFilter();
        NamedSearchFilter filterCriterion2 = new NamedSearchFilter();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);

        namedSearchFilters.add(filterCriterion1);
        namedSearchFilters.add(filterCriterion2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "namedSearchFilters", namedSearchFilters);

        namedSearchFilterGroup.addFilterCriterion(filterCriterion2);

        assertEquals(2, namedSearchFilters.size(), "Filter Criterion is added to the Named Search Filters array multiple times.");
    }

    @Test
    public void testRemoveNamedSearchFilter() {
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        NamedSearchFilter filterCriterion1 = new NamedSearchFilter();
        NamedSearchFilter filterCriterion2 = new NamedSearchFilter();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);

        namedSearchFilters.add(filterCriterion1);
        namedSearchFilters.add(filterCriterion2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "namedSearchFilters", namedSearchFilters);

        namedSearchFilterGroup.removeNamedSearchFilter(filterCriterion2);

        assertFalse(namedSearchFilters.contains(filterCriterion2), "Filter Criterion is still in the Named Search Filters array.");
    }

    @Test
    public void testGetNamedSearchFilterWhenInArray() {
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        NamedSearchFilter filterCriterion1 = new NamedSearchFilter();
        NamedSearchFilter filterCriterion2 = new NamedSearchFilter();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);

        namedSearchFilters.add(filterCriterion1);
        namedSearchFilters.add(filterCriterion2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "namedSearchFilters", namedSearchFilters);

        assertEquals(filterCriterion2, namedSearchFilterGroup.getNamedSearchFilter(filterCriterion2.getId()), "Filter Criterion is not returned.");
    }

    @Test
    public void testGetNamedSearchFilterWhenNotInArray() {
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();
        NamedSearchFilter filterCriterion1 = new NamedSearchFilter();
        NamedSearchFilter filterCriterion2 = new NamedSearchFilter();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);

        namedSearchFilters.add(filterCriterion1);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "namedSearchFilters", namedSearchFilters);

        assertNull(namedSearchFilterGroup.getNamedSearchFilter(filterCriterion2.getId()), "Filter Criterion is returned.");
    }

    @Override
    protected NamedSearchFilterGroup getInstance() {
        return namedSearchFilterGroup;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getPublicFlag", "publicFlag", true),
            Arguments.of("getPublicFlag", "publicFlag", false),
            Arguments.of("getColumnsFlag", "columnsFlag", true),
            Arguments.of("getColumnsFlag", "columnsFlag", false),
            Arguments.of("getUmiRelease", "umiRelease", true),
            Arguments.of("getUmiRelease", "umiRelease", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setPublicFlag", "publicFlag", true),
            Arguments.of("setPublicFlag", "publicFlag", false),
            Arguments.of("setColumnsFlag", "columnsFlag", true),
            Arguments.of("setColumnsFlag", "columnsFlag", false),
            Arguments.of("setUmiRelease", "umiRelease", true),
            Arguments.of("setUmiRelease", "umiRelease", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<SubmissionListColumn> savedColumns = new ArrayList<>();
        Set<NamedSearchFilter> namedSearchFilters = new HashSet<>();

        return Stream.of(
            Arguments.of("user", new User()),
            Arguments.of("name", "value"),
            Arguments.of("sortColumnTitle", "value"),
            Arguments.of("sortDirection", Sort.ASC),
            Arguments.of("savedColumns", savedColumns),
            Arguments.of("namedSearchFilters", namedSearchFilters)
        );
    }

}
