package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;

public interface NamedSearchFilterRepoCustom {
	public NamedSearchFilter create(User creator, String name);
}
