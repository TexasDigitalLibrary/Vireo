package org.tdl.vireo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.service.DefaultFiltersService;
import org.tdl.vireo.service.DefaultSettingsService;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;
import org.tdl.vireo.service.DepositorService;
import org.tdl.vireo.service.EntityControlledVocabularyService;
import org.tdl.vireo.service.ProquestCodesService;
import org.tdl.vireo.service.SystemDataLoader;

@ActiveProfiles(value = { "test" })
@SpringBootTest(classes = { Application.class })
@Transactional(propagation = Propagation.REQUIRES_NEW)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ApplicationInitializationTest {

    @Autowired
    private SystemDataLoader systemDataLoader;

    @Autowired
    private EntityControlledVocabularyService entityControlledVocabularyService;

    @Autowired
    private DefaultSettingsService defaultSettingsService;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionListColumnService;

    @Autowired
    private DefaultFiltersService defaultFiltersService;

    @Autowired
    private DepositorService depositorService;

    @Autowired
    private ProquestCodesService proquesteCodesService;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Test
    public void testLoadSystemData() throws Exception {
        assertInMemorySystemData(false);

        // reload to ensure nothing changes
        systemDataLoader.loadSystemData();
        entityControlledVocabularyService.scanForEntityControlledVocabularies();
        assertInMemorySystemData(true);
    }

    private void assertInMemorySystemData(boolean isReload) throws ClassNotFoundException {
        assertEntityControlledVocabulary(48, "Degrees", isReload);
        assertEntityControlledVocabulary(3, "Graduation Months", isReload);
        assertEntityControlledVocabulary(5, "Proquest Embargos", isReload);
        assertEntityControlledVocabulary(1, "Languages", isReload);
        assertEntityControlledVocabulary(4, "Default Embargos", isReload);

        assertEquals(8, this.defaultSettingsService.getTypes().size(),
            isReload
                ? "Incorrect number of default setting types after reload"
                : "Incorrect number of default setting types");

        assertSettingsType(8, "application", isReload);
        assertSettingsType(4, "footer", isReload);
        assertSettingsType(2, "orcid", isReload);
        assertSettingsType(9, "proquest_umi_degree_code", isReload);
        assertSettingsType(1, "export", isReload);
        assertSettingsType(22, "lookAndFeel", isReload);
        assertSettingsType(1, "submission", isReload);
        assertSettingsType(22, "shibboleth", isReload);

        assertEquals(10, this.defaultSubmissionListColumnService.getDefaultSubmissionListColumns().size(),
            isReload
                ? "Incorrect number of default submission list columns after reload"
                : "Incorrect number of default submission list columns");

        assertEquals(5, this.defaultFiltersService.getDefaultFilter().size(),
            isReload
                ? "Incorrect number of default filters after reload"
                : "Incorrect number of default filters");

        assertNotNull(this.depositorService.getDepositor("SWORDv1Depositor"),
            isReload
                ? "SWORDv1 depositor not found after reload"
                : "SWORDv1 depositor not found");

        assertProquestCodes(82, "languages", isReload);
        assertProquestCodes(1219, "degrees", isReload);
        assertProquestCodes(288, "subjects", isReload);
    }

    private void assertEntityControlledVocabulary(int expected, String name, boolean isReload) throws ClassNotFoundException {
        List<VocabularyWord> dictionary = entityControlledVocabularyService.getControlledVocabularyWords(name);
        assertEquals(expected, dictionary.size(),
            isReload
                ? String.format("Incorrect number of words in %s dictionary after reload", name)
                : String.format("Incorrect number of words in %s dictionary", name));
        ControlledVocabulary cv = controlledVocabularyRepo.findByName(name);
        assertNotNull(cv,
            isReload
                ? String.format("%s dictionary not found after reload", name)
                : String.format("%s dictionary not found", name));
        assertTrue(cv.getIsEntityProperty(),
            isReload
                ? String.format("%s dictionary is not entity property after reload", name)
                : String.format("%s dictionary is not entity property", name));
        assertEquals(expected, cv.getDictionary().size(),
            isReload
                ? String.format("Controlled vocabulary %s dictionary has incorrect number of words after reload", name)
                : String.format("Controlled vocabulary %s dictionary has incorrect number of words", name));
    }

    private void assertSettingsType(int expected, String type, boolean isReload) {
        assertEquals(expected, this.defaultSettingsService.getSettingsByType(type).size(),
            isReload
                ? String.format("Incorrect number of default %s settings after reload", type)
                : String.format("Incorrect number of default %s settings", type));
    }

    private void assertProquestCodes(int expected, String key, boolean isReload) {
        Map<String, String> codes = this.proquesteCodesService.getCodes(key);
        assertNotNull(codes,
            isReload
                ? String.format("Missing proquest %s codes after reload", key)
                : String.format("Missing proquest %s codes", key));
        assertEquals(expected, codes.size(),
            isReload
                ? String.format("Incorrect number of proquest %s codes after reload", key)
                : String.format("Incorrect number of proquest %s codes", key));
    }

}
