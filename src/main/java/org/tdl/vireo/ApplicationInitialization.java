package org.tdl.vireo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.tdl.vireo.service.EntityControlledVocabularyService;
import org.tdl.vireo.service.SystemDataLoader;

@Component
@Profile("!test")
public class ApplicationInitialization implements ApplicationListener<ApplicationReadyEvent> {

    @Lazy
    @Autowired
    private SystemDataLoader systemDataLoader;

    @Lazy
    @Autowired
    private EntityControlledVocabularyService entityControlledVocabularyService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // load defaults first
        systemDataLoader.loadSystemDefaults();
        // assumes one language defined in defaults
        entityControlledVocabularyService.scanForEntityControlledVocabularies(event);
    }

}
