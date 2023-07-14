package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class DocumentTypeTest extends AbstractModelTest<DocumentType> {

    @InjectMocks
    private DocumentType documentType;

    @Test
    public void testDocumentTypeInstantiation() {
        FieldPredicate fieldPredicate = new FieldPredicate();

        fieldPredicate.setId(1L);
        documentType.setName("name");
        documentType.setFieldPredicate(fieldPredicate);

        DocumentType newDocumentType = new DocumentType(documentType.getName(), documentType.getFieldPredicate());

        assertEquals(newDocumentType.getName(), documentType.getName(), "Name does not match.");
        assertEquals(newDocumentType.getFieldPredicate(), documentType.getFieldPredicate(), "Field Predicate does not match.");
    }

    @Override
    protected DocumentType getInstance() {
        return documentType;
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
