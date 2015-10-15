package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;

public interface EmailTemplateRepo extends JpaRepository<EmailTemplate, Long>, EmailTemplateRepoCustom{
	//TODO - what attribute could be used to uniquely find EmailTemplatr
	public EmailTemplate findByName(String name);
}
