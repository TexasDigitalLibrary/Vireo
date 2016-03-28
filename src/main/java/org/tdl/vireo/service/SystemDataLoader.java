package org.tdl.vireo.service;

import java.util.List;

import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.SubmissionState;

public interface SystemDataLoader {
    
    public void loadSystemOrganization();

    public void loadSystemSubmissionStates();
    
    public SubmissionState recursivelyFindOrCreateSubmissionState(SubmissionState submissionState);
    
    public EmailTemplate loadSystemEmailTemplate(String name);
    
    public void generateAllSystemEmailTemplates();
    
    public List<String> getAllSystemEmailTemplateNames();
    
    public void generateAllSystemEmbargos();
    
    public void generateSystemDefaults();
        
}
