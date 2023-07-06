package org.tdl.vireo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tdl.vireo.model.depositor.SWORDv1Depositor;

@Configuration
@ConfigurationProperties(prefix = "vireo.depositor.swordv1")
public class VireoDepositorSwordV1Config {

    private boolean singleUrl = false;

    @Bean
    public SWORDv1Depositor sWORDv1Depositor() {
        return new SWORDv1Depositor();
    }

    public boolean getSingleUrl() {
        return singleUrl;
    }

    public void setSingleUrl(boolean singleUrl) {
        this.singleUrl = singleUrl;
    }

}
