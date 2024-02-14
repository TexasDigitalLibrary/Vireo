package org.tdl.vireo.model.response;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.tamu.weaver.response.ApiView;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.response.Views.Partial;
import org.tdl.vireo.model.response.Views.SubmissionIndividual;
import org.tdl.vireo.model.response.Views.SubmissionIndividualActionLogs;
import org.tdl.vireo.model.response.Views.SubmissionList;

@ActiveProfiles(value = { "test", "isolated-test" })
public class ViewsTest {

    @Test
    public void testViewsInstantiation() {
        assertNotNull(new Views(), "View is null.");
    }

    @Test
    public void testSubmissionIndividualActionLogsInstantiation() {
        SubmissionIndividualActionLogs view = new Views.SubmissionIndividualActionLogs();

        assertNotNull(view, "View is null.");
        assertTrue(view instanceof ApiView.Partial);
    }

    @Test
    public void testSubmissionIndividualInstantiation() {
        SubmissionIndividual view = new Views.SubmissionIndividual();

        assertNotNull(view, "View is null.");
        assertTrue(view instanceof ApiView.Partial);
    }

    @Test
    public void testPartialInstantiation() {
        Partial view = new Views.Partial();

        assertNotNull(view, "View is null.");
        assertTrue(view instanceof ApiView.Partial);
    }

    @Test
    public void testSubmissionListInstantiation() {
        SubmissionList view = new Views.SubmissionList();

        assertNotNull(view, "View is null.");
        assertTrue(view instanceof ApiView.Partial);
    }
}
