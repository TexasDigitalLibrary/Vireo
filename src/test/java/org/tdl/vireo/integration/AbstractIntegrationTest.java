package org.tdl.vireo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.service.DefaultFiltersService;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;
import org.tdl.vireo.service.EntityControlledVocabularyService;
import org.tdl.vireo.service.SystemDataLoader;

@ActiveProfiles(value = { "test", "isolated-test" })
@SpringBootTest(classes = { Application.class })
@Transactional(propagation = Propagation.REQUIRES_NEW)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest extends MockData {

    protected static final String jwtString = "ToY1Y3O6o-iESvV_c3NR3XHvi1vqfYtLIB83HC-d80_qqS98EzfWfPxkP2faL-tEIu9b9wm-q0-T3aNzBWxphQ7ZVxnJSYvtBLzEAh7WVYbezyA3Dgj-nMbsUypCujnuEa0fDwnXXSDr2DRjw4JakMACdPZifN6hcz5-oYFqgWFOPvDE3Gr28ko4XpExXcRaZxYv0p4KW7ISquenB9clu6aKjoKrNwbXqHQvXyk_uFD7GmNuG7RB5Je3jrS0a4q5GSdRyegcoxbGGsNGTKx1f7-f6U4pAHD6fYUmoheZag975Py1Bk8PtUmI8rFWbh8YYPhnnOjGIdJ9qMBFC4-hmVi5hVGhqRdIX9VFeQjxP9gHGqQf5uihmJ8WUY9jh1IP";

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected SystemDataLoader systemDataLoader;

    @Autowired
    protected EntityControlledVocabularyService entityControlledVocabularyService;

    @Autowired
    protected DefaultSubmissionListColumnService defaultSubmissionListColumnService;

    @Autowired
    protected DefaultFiltersService defaultFiltersService;

    protected MockMvc mockMvc;

}
