package org.tdl.vireo.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.SubmissionStatus;

@Service(value = "systemDataLoader")
@Profile(value = { "test" })
public class SystemDataLoaderMock implements SystemDataLoader {

    @Override
    public void loadSystemOrganization() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadSystemSubmissionStatuses() {
        // TODO Auto-generated method stub

    }

    @Override
    public SubmissionStatus recursivelyFindOrCreateSubmissionStatus(SubmissionStatus submissionStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EmailTemplate loadSystemEmailTemplate(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void loadSystemInputTypes() {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateAllSystemEmailTemplates() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getAllSystemEmailTemplateNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void generateAllSystemEmbargos() {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateSystemDefaults() {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateAllOrganizationCategories() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadDefaultControlledVocabularies() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadPackagers() {
        // TODO Auto-generated method stub

    }

}
