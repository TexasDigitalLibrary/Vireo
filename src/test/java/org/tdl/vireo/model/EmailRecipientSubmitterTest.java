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

public class EmailRecipientSubmitterTest extends AbstractModelTest<EmailRecipientSubmitter> {

    @InjectMocks
    private EmailRecipientSubmitter emailRecipientSubmitter;

    @Test
    public void testGetEmails() {
        Submission submission = new Submission();
        User submitter = new User();
        Map<String, String> settings = new HashMap<>();

        // Warning: "prefered" is a typo in the functional code (this should instead be "preferred").
        String key = "preferedEmail";

        settings.put(key, "nobody@example.com");
        submitter.setId(1L);
        submitter.setSettings(settings);

        ReflectionTestUtils.setField(submission, "submitter", submitter);

        List<String> got = emailRecipientSubmitter.getEmails(submission);

        assertNotNull(got, "E-mails array must not be null.");
        assertTrue(got.contains(settings.get(key)), "E-mail is not found.");
    }

    @Override
    protected EmailRecipientSubmitter getInstance() {
        return emailRecipientSubmitter;
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
