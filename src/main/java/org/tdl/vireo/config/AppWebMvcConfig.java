package org.tdl.vireo.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import edu.tamu.framework.config.CoreWebMvcConfig;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class AppWebMvcConfig extends CoreWebMvcConfig {

}