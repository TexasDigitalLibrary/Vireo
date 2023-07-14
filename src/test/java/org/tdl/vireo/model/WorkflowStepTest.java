package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.tamu.weaver.data.model.WeaverEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.inheritance.HeritableComponent;

public class WorkflowStepTest extends AbstractModelCustomMethodTest<WorkflowStep> {

    @InjectMocks
    private WorkflowStep workflowStep;

    @Test
    public void testReplaceOriginalFieldProfileOnFound() {
        List<FieldProfile> originalFPs = new ArrayList<>();
        FieldProfile originalFP1 = new FieldProfile();
        FieldProfile originalFP2 = new FieldProfile();
        FieldProfile newFP = new FieldProfile();

        originalFP1.setId(1L);
        originalFP2.setId(2L);
        newFP.setId(3L);

        originalFPs.add(originalFP1);
        originalFPs.add(originalFP2);

        workflowStep.setOriginalFieldProfiles(originalFPs);
        workflowStep.setAggregateFieldProfiles(originalFPs);

        Boolean found = workflowStep.replaceOriginalFieldProfile(originalFP2, newFP);

        assertTrue(found, "The Field Profile did not get replaced.");

        assertTrue(workflowStep.getOriginalFieldProfiles().contains(newFP), "The new Field Profile is not found.");
        assertFalse(workflowStep.getOriginalFieldProfiles().contains(originalFP2), "The old Field Profile is found.");

        assertTrue(workflowStep.getAggregateFieldProfiles().contains(newFP), "The new Field Profile is not found in the Aggregate Field Profiles.");
        assertFalse(workflowStep.getAggregateFieldProfiles().contains(originalFP2), "The old Field Profile is found in the Aggregate Field Profiles.");
    }

    @Test
    public void testReplaceOriginalFieldProfileOnNotFound() {
        List<FieldProfile> originalFPs = new ArrayList<>();
        FieldProfile originalFP1 = new FieldProfile();
        FieldProfile originalFP2 = new FieldProfile();
        FieldProfile newFP = new FieldProfile();
        FieldProfile otherFP = new FieldProfile();

        originalFP1.setId(1L);
        originalFP2.setId(2L);
        newFP.setId(3L);
        otherFP.setId(4L);

        originalFPs.add(originalFP1);
        originalFPs.add(originalFP2);

        workflowStep.setOriginalFieldProfiles(originalFPs);
        workflowStep.setAggregateFieldProfiles(originalFPs);

        Boolean found = workflowStep.replaceOriginalFieldProfile(otherFP, newFP);

        assertFalse(found, "The Field Profile did get replaced.");

        assertFalse(workflowStep.getOriginalFieldProfiles().contains(newFP), "The new Field Profile is found.");
        assertFalse(workflowStep.getOriginalFieldProfiles().contains(otherFP), "The old Field Profile is found.");

        assertFalse(workflowStep.getAggregateFieldProfiles().contains(newFP), "The new Field Profile is found in the Aggregate Field Profiles.");
        assertFalse(workflowStep.getAggregateFieldProfiles().contains(otherFP), "The old Field Profile is found in the Aggregate Field Profiles.");
    }

    @Test
    public void testGetFieldProfileByPredicateOnFound() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        FieldProfile fieldProfile1 = new FieldProfile();
        FieldProfile fieldProfile2 = new FieldProfile();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();

        fieldProfile1.setId(1L);
        fieldProfile2.setId(2L);

        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(2L);

        fieldPredicate1.setValue("value1");
        fieldPredicate2.setValue("value2");

        fieldProfile1.setFieldPredicate(fieldPredicate1);
        fieldProfile2.setFieldPredicate(fieldPredicate2);

        fieldProfiles.add(fieldProfile1);
        fieldProfiles.add(fieldProfile2);

        workflowStep.setOriginalFieldProfiles(fieldProfiles);
        workflowStep.setAggregateFieldProfiles(fieldProfiles);

        FieldProfile got = workflowStep.getFieldProfileByPredicate(fieldPredicate2);

        assertEquals(got, fieldProfile2, "Did not correctly find Field Profile.");
    }

    @Test
    public void testGetFieldProfileByPredicateOnNotFound() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        FieldProfile fieldProfile1 = new FieldProfile();
        FieldProfile fieldProfile2 = new FieldProfile();
        FieldProfile fieldProfile3 = new FieldProfile();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();
        FieldPredicate fieldPredicate3 = new FieldPredicate();

        fieldProfile1.setId(1L);
        fieldProfile2.setId(2L);
        fieldProfile3.setId(3L);

        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(2L);
        fieldPredicate3.setId(3L);

        fieldPredicate1.setValue("value1");
        fieldPredicate2.setValue("value2");
        fieldPredicate3.setValue("value3");

        fieldProfile1.setFieldPredicate(fieldPredicate1);
        fieldProfile2.setFieldPredicate(fieldPredicate2);
        fieldProfile3.setFieldPredicate(fieldPredicate3);

        fieldProfiles.add(fieldProfile1);
        fieldProfiles.add(fieldProfile2);

        workflowStep.setOriginalFieldProfiles(fieldProfiles);
        workflowStep.setAggregateFieldProfiles(fieldProfiles);

        FieldProfile got = workflowStep.getFieldProfileByPredicate(fieldPredicate3);

        assertNull(got, "Must not find Field Profile.");
    }

    @Test
    public void testReplaceOriginalNoteOnFound() {
        List<Note> originalNotes = new ArrayList<>();
        Note originalNote1 = new Note();
        Note originalNote2 = new Note();
        Note newNote = new Note();

        originalNote1.setId(1L);
        originalNote2.setId(2L);
        newNote.setId(3L);

        originalNotes.add(originalNote1);
        originalNotes.add(originalNote2);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(originalNotes);

        Boolean found = workflowStep.replaceOriginalNote(originalNote2, newNote);

        assertTrue(found, "The Field Profile did not get replaced.");

        assertTrue(workflowStep.getOriginalNotes().contains(newNote), "The new Note is not found.");
        assertFalse(workflowStep.getOriginalNotes().contains(originalNote2), "The old Note is found.");

        assertTrue(workflowStep.getAggregateNotes().contains(newNote), "The new Note is not found in the Aggregate Notes.");
        assertFalse(workflowStep.getAggregateNotes().contains(originalNote2), "The old Note is found in the Aggregate Notes.");
    }

    @Test
    public void testReplaceOriginalNoteOnNotFound() {
        List<Note> originalNotes = new ArrayList<>();
        Note originalNote1 = new Note();
        Note originalNote2 = new Note();
        Note newNote = new Note();
        Note otherNote = new Note();

        originalNote1.setId(1L);
        originalNote2.setId(2L);
        newNote.setId(3L);
        otherNote.setId(4L);

        originalNotes.add(originalNote1);
        originalNotes.add(originalNote2);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(originalNotes);

        Boolean found = workflowStep.replaceOriginalNote(otherNote, newNote);

        assertFalse(found, "The Note did get replaced.");

        assertFalse(workflowStep.getOriginalNotes().contains(newNote), "The new Note is found.");
        assertFalse(workflowStep.getOriginalNotes().contains(otherNote), "The old Note is found.");

        assertFalse(workflowStep.getAggregateNotes().contains(newNote), "The new Note is found in the Aggregate Notes.");
        assertFalse(workflowStep.getAggregateNotes().contains(otherNote), "The old Note is found in the Aggregate Notes.");
    }

    @Test
    public void testAddOriginalHeritableModelUsingNote() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        Note newNote = new Note();

        newNote.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addOriginalHeritableModel(newNote);

        assertTrue(workflowStep.getOriginalNotes().contains(newNote), "The new Note is not found.");
        assertEquals(workflowStep.getOriginalFieldProfiles().size(), 0, "The Original Field Profiles have a non-zero length.");

        assertTrue(workflowStep.getAggregateNotes().contains(newNote), "The new Note is not found in the Aggregate Notes.");
        assertEquals(workflowStep.getAggregateFieldProfiles().size(), 0, "The Aggregate Field Profiles have a non-zero length.");
    }

    @Test
    public void testAddOriginalHeritableModelUsingFieldProfile() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        FieldProfile newFieldProfile = new FieldProfile();

        newFieldProfile.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addOriginalHeritableModel(newFieldProfile);

        assertTrue(workflowStep.getOriginalFieldProfiles().contains(newFieldProfile), "The new Note is not found.");
        assertEquals(workflowStep.getOriginalNotes().size(), 0, "The Original Notes have a non-zero length.");

        assertTrue(workflowStep.getAggregateFieldProfiles().contains(newFieldProfile), "The new Note is not found in the Aggregate Notes.");
        assertEquals(workflowStep.getAggregateNotes().size(), 0, "The Aggregate Notes have a non-zero length.");
    }

    @Test
    public void testAddOriginalHeritableModelUsingOther() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        OtherComponent newComponent = new OtherComponent();

        newComponent.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addOriginalHeritableModel(newComponent);

        assertEquals(workflowStep.getOriginalFieldProfiles().size(), 0, "The Original Field Profiles have a non-zero length.");
        assertEquals(workflowStep.getOriginalNotes().size(), 0, "The Original Notes have a non-zero length.");

        assertEquals(workflowStep.getAggregateFieldProfiles().size(), 0, "The Aggregate Field Profiles have a non-zero length.");
        assertEquals(workflowStep.getAggregateNotes().size(), 0, "The Aggregate Notes have a non-zero length.");
    }

    @Test
    public void testAddAggregateHeritableModelUsingNote() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        Note newNote = new Note();

        newNote.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addAggregateHeritableModel(newNote);

        assertEquals(workflowStep.getOriginalNotes().size(), 0, "The Original Notes have a non-zero length.");
        assertEquals(workflowStep.getOriginalFieldProfiles().size(), 0, "The Original Field Profiles have a non-zero length.");

        assertTrue(workflowStep.getAggregateNotes().contains(newNote), "The new Note is not found in the Aggregate Notes.");
        assertEquals(workflowStep.getAggregateFieldProfiles().size(), 0, "The Aggregate Field Profiles have a non-zero length.");
    }

    @Test
    public void testAddAggregateHeritableModelUsingFieldProfile() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        FieldProfile newFieldProfile = new FieldProfile();

        newFieldProfile.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addAggregateHeritableModel(newFieldProfile);

        assertEquals(workflowStep.getOriginalFieldProfiles().size(), 0, "The Original Field Profiles have a non-zero length.");
        assertEquals(workflowStep.getOriginalNotes().size(), 0, "The Original Notes have a non-zero length.");

        assertTrue(workflowStep.getAggregateFieldProfiles().contains(newFieldProfile), "The new Note is not found in the Aggregate Notes.");
        assertEquals(workflowStep.getAggregateNotes().size(), 0, "The Aggregate Notes have a non-zero length.");
    }

    @Test
    public void testAddAggregateHeritableModelUsingOther() {
        List<Note> originalNotes = new ArrayList<>();
        List<Note> aggregateNotes = new ArrayList<>();
        List<FieldProfile> originalFieldProfiles = new ArrayList<>();
        List<FieldProfile> aggregateFieldProfiles = new ArrayList<>();
        OtherComponent newComponent = new OtherComponent();

        newComponent.setId(1L);

        workflowStep.setOriginalNotes(originalNotes);
        workflowStep.setAggregateNotes(aggregateNotes);

        workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
        workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);

        workflowStep.addAggregateHeritableModel(newComponent);

        assertEquals(workflowStep.getOriginalFieldProfiles().size(), 0, "The Original Field Profiles have a non-zero length.");
        assertEquals(workflowStep.getOriginalNotes().size(), 0, "The Original Notes have a non-zero length.");

        assertEquals(workflowStep.getAggregateFieldProfiles().size(), 0, "The Aggregate Field Profiles have a non-zero length.");
        assertEquals(workflowStep.getAggregateNotes().size(), 0, "The Aggregate Notes have a non-zero length.");
    }

    @Test
    public void testReorderAggregateFieldProfile() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        FieldProfile fieldProfile1 = new FieldProfile();
        FieldProfile fieldProfile2 = new FieldProfile();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();

        fieldProfile1.setId(1L);
        fieldProfile2.setId(2L);

        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(2L);

        fieldPredicate1.setValue("value1");
        fieldPredicate2.setValue("value2");

        fieldProfile1.setFieldPredicate(fieldPredicate1);
        fieldProfile2.setFieldPredicate(fieldPredicate2);

        fieldProfiles.add(fieldProfile1);
        fieldProfiles.add(fieldProfile2);

        ReflectionTestUtils.setField(getInstance(), "aggregateFieldProfiles", fieldProfiles);

        // Warning: The re-order parameters use index + 1 location logic such that 1 represents index 0 and 2 represents index 1.
        workflowStep.reorderAggregateFieldProfile(1, 2);

        assertEquals(fieldProfiles.get(0), fieldProfile2, "Did not correctly re-order Aggregate Field Profile.");
        assertEquals(fieldProfiles.get(1), fieldProfile1, "Did not correctly re-order Aggregate Field Profile.");
    }

    @Test
    public void testReorderAggregateNote() {
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        Note note2 = new Note();

        note1.setId(1L);
        note2.setId(2L);

        notes.add(note1);
        notes.add(note2);

        ReflectionTestUtils.setField(getInstance(), "aggregateNotes", notes);

        // Warning: The re-order parameters use index + 1 location logic such that 1 represents index 0 and 2 represents index 1.
        workflowStep.reorderAggregateNote(1, 2);

        assertEquals(notes.get(0), note2, "Did not correctly re-order Aggregate Note.");
        assertEquals(notes.get(1), note1, "Did not correctly re-order Aggregate Note.");
    }

    @Override
    protected WorkflowStep getInstance() {
        return workflowStep;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getOverrideable", "overrideable", true),
            Arguments.of("getOverrideable", "overrideable", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setOverrideable", "overrideable", true),
            Arguments.of("setOverrideable", "overrideable", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        fieldProfiles.add(new FieldProfile());

        List<Note> notes = new ArrayList<>();
        notes.add(new Note());

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("aggregateFieldProfiles", fieldProfiles),
            Arguments.of("aggregateNotes", notes),
            Arguments.of("instructions", "instruction"),
            Arguments.of("originatingOrganization", new Organization()),
            Arguments.of("originatingWorkflowStep", new WorkflowStep()),
            Arguments.of("originalFieldProfiles", fieldProfiles),
            Arguments.of("originalNotes", notes)
        );
    }

    protected class OtherComponent implements HeritableComponent<Object> {
        private Long id = 0L;

        @Override
        public int compareTo(WeaverEntity arg0) {
            return 0;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long arg0) {
            this.id = arg0;
        }

        @Override
        public void setOriginating(Object originatingHeritableModel) {
        }

        @Override
        public Object getOriginating() {
            return null;
        }

        @Override
        public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        }

        @Override
        public WorkflowStep getOriginatingWorkflowStep() {
            return null;
        }

        @Override
        public Boolean getOverrideable() {
            return null;
        }

        @Override
        public Object clone() {
            return new Object();
        }
    }
}
