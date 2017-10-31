package org.tdl.vireo.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.service.SystemDataLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public abstract class AbstractIntegrationTest extends MockData {

    protected static final String jwtString = "ToY1Y3O6o-iESvV_c3NR3XHvi1vqfYtLIB83HC-d80_qqS98EzfWfPxkP2faL-tEIu9b9wm-q0-T3aNzBWxphQ7ZVxnJSYvtBLzEAh7WVYbezyA3Dgj-nMbsUypCujnuEa0fDwnXXSDr2DRjw4JakMACdPZifN6hcz5-oYFqgWFOPvDE3Gr28ko4XpExXcRaZxYv0p4KW7ISquenB9clu6aKjoKrNwbXqHQvXyk_uFD7GmNuG7RB5Je3jrS0a4q5GSdRyegcoxbGGsNGTKx1f7-f6U4pAHD6fYUmoheZag975Py1Bk8PtUmI8rFWbh8YYPhnnOjGIdJ9qMBFC4-hmVi5hVGhqRdIX9VFeQjxP9gHGqQf5uihmJ8WUY9jh1IP";

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected SystemDataLoader systemDataLoader;

    protected MockMvc mockMvc;

    @Before
    public abstract void setup();

    @After
    public abstract void cleanup();

}
