package org.tdl.vireo.model.listener;

import java.util.ArrayList;

import javax.persistence.PrePersist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.User;
import org.tdl.vireo.service.DefaultFiltersService;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

@Component
public class UserListener {

    @Autowired
    private Environment env;

    @Lazy
    @Autowired
    private DefaultFiltersService defaultFiltersService;

    @Lazy
    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @PrePersist
    private void beforeCreate(User user) {
        if (env.acceptsProfiles(Profiles.of("!isolated-test"))) {
            user.setFilterColumns(new ArrayList<>(defaultFiltersService.getDefaultFilter()));
            user.setSubmissionViewColumns(new ArrayList<>(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns()));
        }
    }

}
