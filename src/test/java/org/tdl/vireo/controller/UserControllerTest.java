package org.tdl.vireo.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class UserControllerTest {
    
    @Test
    @Order(value = 1)
    public void stub() {
        
    }
    
}
