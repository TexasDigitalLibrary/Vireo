package org.tdl.vireo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.filter")
public class AppFilterConfig {

    private String embargoTypeNone;

    private String submissionTypeNone;

    public String getEmbargoTypeNone() {
        return embargoTypeNone;
    }

    public void setEmbargoTypeNone(String embargoTypeNone) {
        this.embargoTypeNone = embargoTypeNone;
    }

    public String getSubmissionTypeNone() {
        return submissionTypeNone;
    }

    public void setSubmissionTypeNone(String submissionTypeNone) {
        this.submissionTypeNone = submissionTypeNone;
    }

}
