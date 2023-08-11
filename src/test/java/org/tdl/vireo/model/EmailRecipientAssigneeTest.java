package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailRecipientAssigneeTest extends AbstractModelTest<EmailRecipientAssignee> {

    @InjectMocks
    private EmailRecipientAssignee emailRecipientAssignee;

    @Test
    public void testGetEmails() {
        Submission submission = new Submission();
        User assignee = new User();
        Map<String, String> settings = new HashMap<>();

        // Warning: "prefered" is a typo in the functional code (this should instead be "preferred").
        String key = "preferedEmail";

        settings.put(key, "nobody@example.com");
        assignee.setId(1L);
        assignee.setSettings(settings);

        ReflectionTestUtils.setField(submission, "assignee", assignee);

        List<String> got = emailRecipientAssignee.getEmails(submission);

        assertNotNull(got, "E-mails array must not be null.");
        assertTrue(got.contains(settings.get(key)), "E-mail is not found.");
    }

    @Override
    protected EmailRecipientAssignee getInstance() {
        return emailRecipientAssignee;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value")
        );
    }

}
