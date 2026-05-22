package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.tdl.vireo.model.response.Views;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CustomActionDefinitionTest extends AbstractModelCustomMethodTest<CustomActionDefinition> {

    @InjectMocks
    private CustomActionDefinition customActionDefinition;

    @Override
    protected CustomActionDefinition getInstance() {
        return customActionDefinition;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return getParameterMethodStream();
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return getParameterMethodStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("label", "value")
        );
    }

    private static Stream<Arguments> getParameterMethodStream() {
        return Stream.of(
            Arguments.of("isStudentVisible", "isStudentVisible", true),
            Arguments.of("isStudentVisible", "isStudentVisible", false)
        );
    }

    /**
     * Regression test for GitHub issue #2104.
     *
     * Verifies that {@code CustomActionDefinition.position} is included in the
     * JSON output when serialized under {@link Views.SubmissionList} (and its
     * sub-views such as {@link Views.SubmissionIndividualActionLogs}).
     *
     * The application sets {@code jackson.mapper.default-view-inclusion: false},
     * so any field without a matching {@code @JsonView} annotation is silently
     * excluded when a view is active. The fix overrides {@code getPosition()} in
     * {@code CustomActionDefinition} with {@code @JsonView(Views.SubmissionList.class)}.
     * Removing that annotation will cause this test to fail.
     */
    @Test
    public void testPositionSerializedInSubmissionListView() throws Exception {
        // Configure ObjectMapper to match application.yml: default-view-inclusion: false
        ObjectMapper mapper = JsonMapper.builder()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

        CustomActionDefinition definition = new CustomActionDefinition("Test action", true);
        definition.setPosition(3L);

        // Serialize under Views.SubmissionList — the view used by /submission/query
        String json = mapper.writerWithView(Views.SubmissionList.class).writeValueAsString(definition);
        ObjectNode node = (ObjectNode) mapper.readTree(json);

        assertNotNull(node.get("position"),
            "position must be present in SubmissionList view JSON; " +
            "ensure getPosition() in CustomActionDefinition is annotated with @JsonView(Views.SubmissionList.class)");
        assertTrue(node.get("position").isNumber(),
            "position must be a numeric value in SubmissionList view JSON");
    }

    /**
     * Verifies that {@code position} is also included when serialized under the
     * {@link Views.SubmissionIndividualActionLogs} view used by the admin
     * {@code GET /submission/get-one/{id}} endpoint, since that view extends
     * {@link Views.SubmissionList}.
     */
    @Test
    public void testPositionSerializedInSubmissionIndividualActionLogsView() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

        CustomActionDefinition definition = new CustomActionDefinition("Test action", true);
        definition.setPosition(5L);

        String json = mapper.writerWithView(Views.SubmissionIndividualActionLogs.class).writeValueAsString(definition);
        ObjectNode node = (ObjectNode) mapper.readTree(json);

        assertNotNull(node.get("position"),
            "position must be present in SubmissionIndividualActionLogs view JSON");
        assertTrue(node.get("position").isNumber(),
            "position must be a numeric value in SubmissionIndividualActionLogs view JSON");
    }

}
