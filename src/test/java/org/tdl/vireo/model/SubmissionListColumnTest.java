package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

public class SubmissionListColumnTest extends AbstractModelCustomMethodTest<SubmissionListColumn> {

    @InjectMocks
    private SubmissionListColumn submissionListColumn;

    @Test
    public void testAddFilter() {
        Set<String> filters = new HashSet<>();
        String filter = "filter";

        ReflectionTestUtils.setField(submissionListColumn, "filters", filters);

        submissionListColumn.addFilter(filter);

        assertTrue(filters.contains(filter), "The Filter is not added.");
    }

    @Test
    public void testAddFilterWithExisting() {
        Set<String> filters = new HashSet<>();
        String filter = "filter";

        ReflectionTestUtils.setField(submissionListColumn, "filters", filters);

        submissionListColumn.addFilter(filter);
        submissionListColumn.addFilter(filter);

        assertEquals(1, filters.size(), "The Filter is not added twice.");
    }

    @Test
    public void testAddAllFilters() {
        Set<String> filters = new HashSet<>();
        Set<String> newFilters = new HashSet<>();
        String filter1 = "filter1";
        String filter2 = "filter2";
        String filter3 = "filter3";
        String filter4 = "filter4";

        filters.add(filter1);
        filters.add(filter2);
        newFilters.add(filter2);
        newFilters.add(filter3);
        newFilters.add(filter4);

        ReflectionTestUtils.setField(submissionListColumn, "filters", filters);

        submissionListColumn.addAllFilters(newFilters);

        assertEquals(4, filters.size(), "The Filters array has an incorrect size.");
    }

    @Test
    public void testRemoveFilter() {
        Set<String> filters = new HashSet<>();
        String filter1 = "filter1";
        String filter2 = "filter2";

        filters.add(filter1);
        filters.add(filter2);

        ReflectionTestUtils.setField(submissionListColumn, "filters", filters);

        submissionListColumn.removeFilter(filter2);

        assertFalse(filters.contains(filter2), "The Filter is not removed.");
    }

    @Override
    protected SubmissionListColumn getInstance() {
        return submissionListColumn;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getVisible", "visible", true),
            Arguments.of("getVisible", "visible", false),
            Arguments.of("getExactMatch", "exactMatch", true),
            Arguments.of("getExactMatch", "exactMatch", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setVisible", "visible", true),
            Arguments.of("setVisible", "visible", false),
            Arguments.of("setExactMatch", "exactMatch", true),
            Arguments.of("setExactMatch", "exactMatch", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<String> valuePath = new ArrayList<>();
        valuePath.add("value");

        Set<String> filters = new HashSet<>();
        filters.add("value");

        return Stream.of(
            Arguments.of("inputType", new InputType()),
            Arguments.of("title", "value"),
            Arguments.of("predicate", "value"),
            Arguments.of("valuePath", valuePath),
            Arguments.of("filters", filters),
            Arguments.of("sortOrder", 123),
            Arguments.of("sort", Sort.ASC),
            Arguments.of("sort", Sort.DESC),
            Arguments.of("status", "value")
        );
    }

}
