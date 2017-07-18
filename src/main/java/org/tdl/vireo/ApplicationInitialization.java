package org.tdl.vireo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.tdl.vireo.service.SystemDataLoader;

@Component
@Profile("!test")
public class ApplicationInitialization implements CommandLineRunner {

    @Lazy
    @Autowired
    private SystemDataLoader systemDataLoader;

    @Override
    public void run(String... arg0) throws Exception {
        systemDataLoader.loadSystemDefaults();
    }

}
