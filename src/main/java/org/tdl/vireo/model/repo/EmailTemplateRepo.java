package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;

public interface EmailTemplateRepo extends JpaRepository<EmailTemplate, Long>, EmailTemplateRepoCustom {

    public List<EmailTemplate> findByName(String name);

    public EmailTemplate findByNameAndIsSystemRequired(String name, Boolean isSystemRequired);

    public EmailTemplate findByPosition(Long position);

    public List<EmailTemplate> findAllByOrderByPositionAsc();

}
