package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;

@ActiveProfiles(value = { "test" })
@SpringBootTest(classes = { Application.class })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SystemDataLoaderTest {

    @Autowired
    private SystemDataLoader systemDataLoader;

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

    @Test
    public void testLoadSystemData() throws Exception {
        assertPersistedSystemData(false);

        // reload to ensure nothing changes
        // technically the cache clears after restart
        // however, this is still an undesired side effect of load system data
        systemDataLoader.loadSystemData();
        assertPersistedSystemData(true);
    }

    private void assertPersistedSystemData(boolean isReload) {
        assertEquals(8, this.defaultSettingsService.getTypes().size(),
            isReload
                ? "Incorrect number of default setting types after reload"
                : "Incorrect number of default setting types");

        assertSettingsType(8, "application", isReload);
        assertSettingsType(4, "footer", isReload);
        assertSettingsType(2, "orcid", isReload);
        assertSettingsType(9, "proquest_umi_degree_code", isReload);
        assertSettingsType(1, "export", isReload);
        assertSettingsType(23, "lookAndFeel", isReload);
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

        assertProquestCodes(82, "languages", isReload);
        assertProquestCodes(1219, "degrees", isReload);
        assertProquestCodes(288, "subjects", isReload);
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
