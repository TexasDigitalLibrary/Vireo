package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailRecipientContactTest extends AbstractModelTest<EmailRecipientContact> {

    @InjectMocks
    private EmailRecipientContact emailRecipientContact;

    @Mock
    private Submission submission;

    @Test
    public void testGetEmails() {
        List<String> emails = new ArrayList<String>();
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        FieldValue fieldValue = new FieldValue();
        FieldValue otherFieldValue = new FieldValue();
        FieldPredicate fieldPredicate = new FieldPredicate();

        emails.add("first@example.com");
        emails.add("second@example.com");
        otherFieldValue.setId(1L);
        otherFieldValue.setValue("value1");
        fieldValue.setId(2L);
        fieldValue.setValue("value2");
        fieldValue.setContacts(emails);
        fieldValues.add(fieldValue);
        fieldValues.add(otherFieldValue);
        fieldPredicate.setId(1L);

        ReflectionTestUtils.setField(emailRecipientContact, "fieldPredicate", fieldPredicate);

        when(submission.getFieldValuesByPredicate(any(FieldPredicate.class))).thenReturn(fieldValues);

        List<String> got = emailRecipientContact.getEmails(submission);

        assertEquals(got.size(), emails.size(), "Emails array does not have the correct length.");

        emails.forEach(email -> {
            String found = null;

            for (String e : got) {
                if (e == email) {
                    found = e;
                    break;
                }
            }

            assertNotNull(found, "Did not find e-mail '" + email + "' in the returned array.");
        });
    }

    @Override
    protected EmailRecipientContact getInstance() {
        return emailRecipientContact;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("fieldPredicate", new FieldPredicate())
        );
    }

}
