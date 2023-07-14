package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailRecipientOrganizationTest extends AbstractModelTest<EmailRecipientOrganization> {

    // Warning: @InjectMocks throws NPE here and so an explicit assignment is provided. 
    @InjectMocks
    private EmailRecipientOrganization emailRecipientOrganization = new EmailRecipientOrganization();

    @Test
    public void testGetEmails() {
        Submission submission = new Submission();
        Organization organization = new Organization();
        String email = "nobody@example.com";

        organization.addEmail(email);

        ReflectionTestUtils.setField(emailRecipientOrganization, "organization", organization);

        List<String> got = emailRecipientOrganization.getEmails(submission);

        assertNotNull(got, "E-mails array must not be null.");
        assertTrue(got.contains(email), "E-mail is not found.");
    }

    @Override
    protected EmailRecipientOrganization getInstance() {
        return emailRecipientOrganization;
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
