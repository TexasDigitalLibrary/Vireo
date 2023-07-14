package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailRecipientPlainAddressTest extends AbstractModelTest<EmailRecipientPlainAddress> {

    @InjectMocks
    private EmailRecipientPlainAddress emailRecipientPlainAddress;

    @Test
    public void testGetEmails() {
        Submission submission = new Submission();
        String email = "nobody@example.com";

        ReflectionTestUtils.setField(emailRecipientPlainAddress, "name", email);

        List<String> got = emailRecipientPlainAddress.getEmails(submission);

        assertNotNull(got, "E-mails array must not be null.");
        assertTrue(got.contains(email), "E-mail is not found.");
    }

    @Override
    protected EmailRecipientPlainAddress getInstance() {
        return emailRecipientPlainAddress;
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
