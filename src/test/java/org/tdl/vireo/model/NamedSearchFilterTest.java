package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class NamedSearchFilterTest extends AbstractModelCustomMethodTest<NamedSearchFilter> {

    @InjectMocks
    private NamedSearchFilter namedSearchFilter;

    @Test
    public void testAddFilterCriterion() {
        Set<FilterCriterion> filters = new HashSet<>();
        FilterCriterion filterCriterion1 = new FilterCriterion();
        FilterCriterion filterCriterion2 = new FilterCriterion();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);
        filters.add(filterCriterion1);

        ReflectionTestUtils.setField(namedSearchFilter, "filterCriteria", filters);

        namedSearchFilter.addFilter(filterCriterion2);

        assertTrue(filters.contains(filterCriterion2), "Filter Criterion 2 is not found.");
    }

    @Test
    public void testRemoveFilterCriterion() {
        Set<FilterCriterion> filters = new HashSet<>();
        FilterCriterion filterCriterion1 = new FilterCriterion();
        FilterCriterion filterCriterion2 = new FilterCriterion();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);
        filters.add(filterCriterion1);
        filters.add(filterCriterion2);

        ReflectionTestUtils.setField(namedSearchFilter, "filterCriteria", filters);

        namedSearchFilter.removeFilter(filterCriterion2);

        assertFalse(filters.contains(filterCriterion2), "Filter Criterion 2 is found.");
    }

    @Test
    public void testGetFilterValues() {
        Set<FilterCriterion> filters = new HashSet<>();
        FilterCriterion filterCriterion1 = new FilterCriterion();
        FilterCriterion filterCriterion2 = new FilterCriterion();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);
        filterCriterion1.setValue("value1");
        filterCriterion2.setValue("value2");
        filters.add(filterCriterion1);
        filters.add(filterCriterion2);

        ReflectionTestUtils.setField(namedSearchFilter, "filterCriteria", filters);

        Set<String> filterValues = namedSearchFilter.getFilterValues();

        assertNotNull(filterValues, "Returned Filter Values array is null.");
        assertEquals(filters.size(), filterValues.size(), "Returned Filter Values array is of the wrong size.");
        assertTrue(filterValues.contains(filterCriterion1.getValue()), "Filter Criterion 1 value is not found.");
        assertTrue(filterValues.contains(filterCriterion2.getValue()), "Filter Criterion 2 value is not found.");
    }

    @Test
    public void testGetFilterGlosses() {
        Set<FilterCriterion> filters = new HashSet<>();
        FilterCriterion filterCriterion1 = new FilterCriterion();
        FilterCriterion filterCriterion2 = new FilterCriterion();

        filterCriterion1.setId(1L);
        filterCriterion2.setId(2L);
        filterCriterion1.setGloss("gloss1");
        filterCriterion2.setGloss("gloss2");
        filters.add(filterCriterion1);
        filters.add(filterCriterion2);

        ReflectionTestUtils.setField(namedSearchFilter, "filterCriteria", filters);

        Set<String> filterGlosses = namedSearchFilter.getFilterGlosses();

        assertNotNull(filterGlosses, "Returned Filter Glosses array is null.");
        assertEquals(filters.size(), filterGlosses.size(), "Returned Filter Glosses array is of the wrong size.");
        assertTrue(filterGlosses.contains(filterCriterion1.getGloss()), "Filter Criterion 1 gloss is not found.");
        assertTrue(filterGlosses.contains(filterCriterion2.getGloss()), "Filter Criterion 2 gloss is not found.");
    }

    @Override
    protected NamedSearchFilter getInstance() {
        return namedSearchFilter;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        Set<FilterCriterion> filters = new HashSet<>();

        return Stream.of(
            Arguments.of("getAllColumnSearch", "allColumnSearch", true),
            Arguments.of("getAllColumnSearch", "allColumnSearch", false),
            Arguments.of("getExactMatch", "exactMatch", true),
            Arguments.of("getExactMatch", "exactMatch", false),
            Arguments.of("getFilters", "filterCriteria", filters) // Warning: This function is identical to getFilterCriteria().
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        Set<FilterCriterion> filters = new HashSet<>();

        return Stream.of(
            Arguments.of("setAllColumnSearch", "allColumnSearch", true),
            Arguments.of("setAllColumnSearch", "allColumnSearch", false),
            Arguments.of("setExactMatch", "exactMatch", true),
            Arguments.of("setExactMatch", "exactMatch", false),
            Arguments.of("setFilters", "filterCriteria", filters) // Warning: This function is identical to setFilterCriteria().
        );
    }

    private static Stream<Arguments> getParameterStream() {
        Set<FilterCriterion> filterCriteria = new HashSet<>();

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("submissionListColumn", new SubmissionListColumn()),
            Arguments.of("filterCriteria", filterCriteria)
        );
    }

}
