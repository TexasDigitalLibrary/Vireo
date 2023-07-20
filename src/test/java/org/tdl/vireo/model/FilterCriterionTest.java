package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class FilterCriterionTest extends AbstractModelTest<FilterCriterion> {

    @InjectMocks
    private FilterCriterion filterCriterion;

    @Test
    public void testFilterCriterionInstantiation1() {
        filterCriterion.setValue("value");
        filterCriterion.setGloss("gloss");

        FilterCriterion newFilterCriterion = new FilterCriterion(filterCriterion.getValue());

        assertEquals(newFilterCriterion.getValue(), filterCriterion.getValue(), "Value does not match.");
    }

    @Test
    public void testFilterCriterionInstantiation2() {
        filterCriterion.setValue("value");
        filterCriterion.setGloss("gloss");;

        FilterCriterion newFilterCriterion = new FilterCriterion(filterCriterion.getValue(), filterCriterion.getGloss());

        assertEquals(newFilterCriterion.getValue(), filterCriterion.getValue(), "Value does not match.");
        assertEquals(newFilterCriterion.getGloss(), filterCriterion.getGloss(), "Gloss does not match.");
    }

    @Override
    protected FilterCriterion getInstance() {
        return filterCriterion;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("value", "value"),
            Arguments.of("gloss", "value")
        );
    }

}
