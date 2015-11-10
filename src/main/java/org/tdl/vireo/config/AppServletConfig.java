package org.tdl.vireo.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppServletConfig {
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new ServletCustomizer();
    }
    
    private static class ServletCustomizer implements EmbeddedServletContainerCustomizer {  

        @Override
        public void customize(ConfigurableEmbeddedServletContainer cesc) {
            
        }   
        
    }
    
}
