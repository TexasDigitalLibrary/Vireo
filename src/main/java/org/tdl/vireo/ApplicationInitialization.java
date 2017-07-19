package org.tdl.vireo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.tdl.vireo.service.EntityControlledVocabularyService;
import org.tdl.vireo.service.SystemDataLoader;

@Component
@Profile("!test")
public class ApplicationInitialization implements CommandLineRunner {

    @Lazy
    @Autowired
    private SystemDataLoader systemDataLoader;

    @Lazy
    @Autowired
    private EntityControlledVocabularyService entityControlledVocabularyService;

    @Override
    public void run(String... arg0) throws Exception {
        // load defaults first
        systemDataLoader.loadSystemDefaults();
        // assumes one language defined in defaults
        entityControlledVocabularyService.scanForEntityControlledVocabularies();
    }

}
