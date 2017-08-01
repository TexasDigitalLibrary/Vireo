package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterGroupRepoCustom;

public interface NamedSearchFilterGroupRepo extends JpaRepository<NamedSearchFilterGroup, Long>, NamedSearchFilterGroupRepoCustom {

    public List<NamedSearchFilterGroup> findByPublicFlagTrue();

    public NamedSearchFilterGroup findByNameAndPublicFlagTrue(String name);

    public void delete(NamedSearchFilterGroup NamedSearchFilterGroup);

}
