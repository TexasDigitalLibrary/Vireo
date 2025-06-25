package org.tdl.vireo.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.AbstractModelTest;

public class FilteredPageRequestTest extends AbstractModelTest<FilteredPageRequest> {

    @InjectMocks
    private FilteredPageRequest filteredPageRequest;

    @Test
    public void testGetPageRequest() {
        List<DirectionSort> sort = new ArrayList<>();
        sort.add(new DirectionSort("property", Sort.Direction.ASC));

        ReflectionTestUtils.setField(filteredPageRequest, "pageNumber", 0);
        ReflectionTestUtils.setField(filteredPageRequest, "pageSize", 1);
        ReflectionTestUtils.setField(filteredPageRequest, "sort", sort);

        PageRequest pageRequest = filteredPageRequest.getPageRequest();

        assertNotEquals(Sort.unsorted(), pageRequest.getSort(), "Page Request is unsorted.");
    }

    @Test
    public void testGetPageRequestWithoutSort() {
        List<DirectionSort> sort = new ArrayList<>();

        ReflectionTestUtils.setField(filteredPageRequest, "pageNumber", 0);
        ReflectionTestUtils.setField(filteredPageRequest, "pageSize", 1);
        ReflectionTestUtils.setField(filteredPageRequest, "sort", sort);

        PageRequest pageRequest = filteredPageRequest.getPageRequest();

        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageRequest.getSort(), "Default sort order is not ASC by id.");
    }

    @Override
    protected FilteredPageRequest getInstance() {
        return filteredPageRequest;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        List<DirectionSort> sort = new ArrayList<>();
        Map<String, String[]> filters = new HashMap<>();

        sort.add(new DirectionSort());
        filters.put("a", new String[] { "b" });

        return Stream.of(
            Arguments.of("pageNumber", 123),
            Arguments.of("pageSize", 123),
            Arguments.of("sort", sort),
            Arguments.of("filters", filters)
        );
    }

}
