package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.enums.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldProfileTest {

    static final String TEST_FIELD_GLOSS_VALUE = "Test Gloss";
    static final String TEST_FIELD_PREDICATE_VALUE = "dc.test.predicate";
    static final String TEST_CONTROLLED_VOCABULARY_NAME = "Test Controlled Vocaublary";

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private FieldGlossRepo fieldGlossRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @BeforeClass
    public static void init() {
    }

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, fieldProfileRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        // confirm the addition of 1 item to the repo
        assertEquals("The repository did not save the entity!", 1, fieldProfileRepo.count());

        assertFieldProfileIdentity(fieldProfile);

    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        createPopulatedFieldProfile();
        try {
            createPopulatedFieldProfile();
        } catch (Exception e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Test
    @Order(value = 3)
    @Transactional
    public void testFind() {
        FieldProfile fieldProfile = createPopulatedFieldProfile();

        fieldProfile = fieldProfileRepo.findOne(fieldProfile.getId());

        assertNotEquals("Did not find entity!", null, fieldProfile);
        assertFieldProfileIdentity(fieldProfile);
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        FieldProfile fieldProfile = createPopulatedFieldProfile();
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Test
    @Order(value = 5)
    @Transactional
    public void testCascadePersist() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        FieldGloss fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);

        FieldGloss fieldGlossFromRepo;

        // Test PERSIST on fieldGloss
        String newValue = "New Gloss Value";
        fieldGlossFromProfile.setValue(newValue);
        fieldProfileRepo.save(fieldProfile);
        fieldGlossFromRepo = fieldGlossRepo.findOne(fieldGlossFromProfile.getId());

        assertEquals("The Gloss value did not persist parent to child!", newValue, fieldGlossFromRepo.getValue());

        fieldGlossFromRepo.setValue(TEST_FIELD_GLOSS_VALUE);
        fieldGlossRepo.save(fieldGlossFromRepo);
        fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);

        assertEquals("The Gloss value did not persist child to parent!", TEST_FIELD_GLOSS_VALUE, fieldGlossFromProfile.getValue());

    }

    @Test
    @Order(value = 6)
    public void testCascadeMerge() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        // Create Entities to merge
        FieldGloss fieldGlossToMerge = new FieldGloss("New Merge Field Gloss");
        FieldPredicate fieldPredicateToMerge = new FieldPredicate("dc.merge.predicate");
        ControlledVocabulary controlledVocabularyToMerge = new ControlledVocabulary("New Merge Controlled Vocabulary");

        // Add new entities to field profile
        fieldProfile.addFieldGloss(fieldGlossToMerge);
        fieldProfile.setPredicate(fieldPredicateToMerge);
        fieldProfile.addControlledVocabulary(controlledVocabularyToMerge);

        fieldProfileRepo.save(fieldProfile);

        // Test MERGE on fieldGloss, fieldPredicate and controlledVocabulary
        assertEquals("The new field gloss was not merged!", 2, fieldGlossRepo.count());
        assertEquals("The new field predicate was not merged!", 2, fieldPredicateRepo.count());
        assertEquals("The new controlled vocabulary was not merged!", 2, controlledVocabularyRepo.count());
    }

    @Test
    @Order(value = 7)
    @Transactional
    public void testCascadeRefresh() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        FieldGloss fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);
        FieldPredicate fieldPredicateFromProfile = fieldProfile.getPredicate();
        ControlledVocabulary controlledVocabularyFromProfile = fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME);

        // Get children entities from db
        FieldGloss fieldGlossFromRepo = fieldGlossRepo.findOne(fieldGlossFromProfile.getId());
        FieldPredicate fieldPredicateFromRepo = fieldPredicateRepo.findOne(fieldPredicateFromProfile.getId());
        ControlledVocabulary controlledVocabularyFromRepo = controlledVocabularyRepo.findOne(controlledVocabularyFromProfile.getId());

        // Change the entities directly
        fieldGlossFromRepo.setValue("New Value");
        fieldGlossRepo.save(fieldGlossFromRepo);

        fieldPredicateFromRepo.setValue("New Value");
        fieldPredicateRepo.save(fieldPredicateFromRepo);

        controlledVocabularyFromRepo.setName("New Name");
        controlledVocabularyRepo.save(controlledVocabularyFromRepo);

        // Grab the entities through the parent again
        fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);
        fieldPredicateFromProfile = fieldProfile.getPredicate();
        controlledVocabularyFromProfile = fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME);

        // Test REFRESH on fieldGloss, fieldPredicate and controlledVocabulary
        assertNotEquals("Changes to field gloss did not cascade on refresh!", TEST_FIELD_GLOSS_VALUE, fieldGlossFromProfile.getValue());
        assertNotEquals("Changes to field profile did not cascade on refresh!", TEST_FIELD_PREDICATE_VALUE, fieldPredicateFromProfile.getValue());
        assertEquals("Changes to controlled vocabulary did not cascade on refresh!", null, controlledVocabularyFromProfile);

    }

    @Test
    @Order(value = 8)
    public void testCascadeDetach() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        String replaceFieldPredicateValue = "Replace Field Predicate";

        FieldGloss fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);
        FieldPredicate replaceFieldPredicate = fieldPredicateRepo.create(replaceFieldPredicateValue);
        ControlledVocabulary controlledVocabularyFromProfile = fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME);

        long fieldGlossFromProfileId = fieldGlossFromProfile.getId();
        long controlledVocabularyFromProfileId = controlledVocabularyFromProfile.getId();

        fieldProfile.setPredicate(replaceFieldPredicate);
        fieldProfile.removeFieldGloss(fieldGlossFromProfile);
        fieldProfile.removeControlledVocabulary(controlledVocabularyFromProfile);

        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // Test DETACH on fieldGloss
        assertEquals("The remove method deleted the field gloss!", 1, fieldGlossRepo.count());
        assertEquals("The remove method deleted the field predicate!", 2, fieldPredicateRepo.count());
        assertEquals("The remove method deleted the controlled vocabulary!", 1, controlledVocabularyRepo.count());

        FieldPredicate fieldPredicate = fieldProfile.getPredicate();

        assertEquals("The detachment of the field gloss did not persist!!", null, fieldProfile.getFieldGlossByLanguage(Language.ENGLISH));
        assertEquals("The detachment of the field predicate did not persist!", replaceFieldPredicateValue, fieldPredicate.getValue());
        assertEquals("The detachment of the controlled vocabulary did not persist!!", null, fieldProfile.getControlledVocabularyById(controlledVocabularyFromProfileId));

    }

    @Test
    @Order(value = 9)
    public void testCascadeRemove() {

        FieldProfile fieldProfile = createPopulatedFieldProfile();

        FieldGloss fieldGlossFromProfile = fieldProfile.getFieldGlossByLanguage(Language.ENGLISH);
        FieldPredicate fieldPredicateFromProfile = fieldProfile.getPredicate();
        ControlledVocabulary controlledVocabularyFromProfile = fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME);

        fieldProfileRepo.delete(fieldProfile);

        FieldGloss fieldGlossFromRepo = fieldGlossRepo.findOne(fieldGlossFromProfile.getId());
        FieldPredicate fieldPredicateFromRepo = fieldPredicateRepo.findOne(fieldPredicateFromProfile.getId());
        ControlledVocabulary controlledVocabularyFromRepo = controlledVocabularyRepo.findOne(controlledVocabularyFromProfile.getId());

        // Test REMOVE on fieldGloss
        assertNotEquals("The removal of the field profile did cascade to field gloss!", null, fieldGlossFromRepo);
        assertNotEquals("The removal of the field profile did cascade to field predicate!", null, fieldPredicateFromRepo);
        assertNotEquals("The removal of the field profile did cascade to controlled vocabulary!", null, controlledVocabularyFromRepo);

    }

    @After
    public void cleanUp() {
        fieldProfileRepo.deleteAll();
        fieldGlossRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();
    }

    private FieldProfile createPopulatedFieldProfile() {
        // Create all properties for our new field profile
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE); // creates with a default language of English
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        InputType inputType = InputType.INPUT_TEXT;

        // Create a new field profile and populate it will the new properties
        FieldProfile fieldprofile = fieldProfileRepo.create(fieldPredicate, inputType, false, true);
        fieldprofile.addFieldGloss(fieldGloss);
        fieldprofile.addControlledVocabulary(controlledVocabulary);
        fieldprofile = fieldProfileRepo.save(fieldprofile);

        return fieldprofile;
    }

    private void assertFieldProfileIdentity(FieldProfile fieldProfile) {
        // confirm the entity's identity
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PREDICATE_VALUE, fieldProfile.getPredicate().getValue());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_GLOSS_VALUE, fieldProfile.getFieldGlossByLanguage(Language.ENGLISH).getValue());
        assertEquals("The field predicate did not contain the correct value!", TEST_CONTROLLED_VOCABULARY_NAME, fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME).getName());
        assertEquals("The field predicate did not contain the correct value!", false, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", true, fieldProfile.getRequired());
    }

}
