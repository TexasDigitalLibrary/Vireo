package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface EmailTemplateRepo extends WeaverOrderedRepo<EmailTemplate>, EmailTemplateRepoCustom {

    public List<EmailTemplate> findByName(String name);

    public EmailTemplate findByNameAndSystemRequired(String name, Boolean isSystemRequired);

}
