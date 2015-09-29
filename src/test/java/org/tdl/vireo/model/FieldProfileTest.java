package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldProfileTest {

    private static final boolean TEST_FIELD_PROFILE_REPEATABLE = true;
    private static final boolean TEST_FIELD_PROFILE_REQUIRED   = true;
    
    private static final InputType TEST_FIELD_PROFILE_INPUT_TYPE = InputType.INPUT_TEXT;

    private static final String TEST_LANGUAGE = "English";
    
    private static final String TEST_FIELD_PREDICATE_VALUE = "dc.test.predicate";
    
    private static final String TEST_FIELD_GLOSS_VALUE          = "Test Gloss";
    private static final String TEST_CONTROLLED_VOCABULARY_NAME = "Test Controlled Vocaublary";

    private static final String TEST_DETACHABLE_FIELD_GLOSS_VALUE          = "Test Detachable Gloss";
    private static final String TEST_DETACHABLE_CONTROLLED_VOCABULARY_NAME = "Test Detachable Controlled Vocaublary";

    private Language language;
    private FieldPredicate fieldPredicate;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private FieldGlossRepo fieldGlossRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;
    
    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Before
    public void setUp() {
        language = languageRepo.create(TEST_LANGUAGE);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
    }

    @Test
    @Order(value = 1)
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
        assertEquals("The repository did not save the entity!", 1, fieldProfileRepo.count());
        assertEquals("The field profile did not contain the correct perdicate value!", fieldPredicate, fieldProfile.getPredicate());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REQUIRED, fieldProfile.getRequired());
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
        try {
            fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Test
    @Order(value = 3)
    @Transactional
    public void testFind() {
        fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
        FieldProfile fieldProfile = fieldProfileRepo.findByPredicate(fieldPredicate);
        assertNotEquals("Did not find entity!", null, fieldProfile);
        assertEquals("The field profile did not contain the correct perdicate value!", fieldPredicate, fieldProfile.getPredicate());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REQUIRED, fieldProfile.getRequired());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);

        // add glosses and controlled vocabularies
        FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
        FieldGloss detachableFieldGloss = fieldGlossRepo.create(TEST_DETACHABLE_FIELD_GLOSS_VALUE, language);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        ControlledVocabulary detachablecontrolledVocabulary = controlledVocabularyRepo.create(TEST_DETACHABLE_CONTROLLED_VOCABULARY_NAME, language);
        fieldProfile.addFieldGloss(fieldGloss);
        fieldProfile.addControlledVocabulary(controlledVocabulary);
        fieldProfile.addFieldGloss(detachableFieldGloss);
        fieldProfile.addControlledVocabulary(detachablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // verify field glosses and controlled vocabularies
        assertEquals("The field profile did not contain the correct field gloss!", fieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[1]);
        assertEquals("The field profile did not contain the correct detachable field gloss!", detachableFieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[0]);
        assertEquals("The field profile did not contain the correct controlled vocabulary!", controlledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals("The field profile did not contain the correct detachable controlled vocabulary!", detachablecontrolledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_DETACHABLE_CONTROLLED_VOCABULARY_NAME));

        // test detach detachable gloss
        fieldProfile.removeFieldGloss(detachableFieldGloss);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getFieldGlosses().size());

        // test detach detachable controlled vocabularies
        fieldProfile.removeControlledVocabulary(detachablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getControlledVocabularies().size());

        // test delete profile
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("An field profile was not deleted!", 0, fieldProfileRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
        assertEquals("The field predicate was deleted!", 1, fieldPredicateRepo.count());
        assertEquals("The field glosses were deleted!", 2, fieldGlossRepo.count());
        assertEquals("The controlled vocabularies were deleted!", 2, controlledVocabularyRepo.count());
    }

    @After
    public void cleanUp() {
        fieldProfileRepo.deleteAll();
        fieldGlossRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
    }
}
