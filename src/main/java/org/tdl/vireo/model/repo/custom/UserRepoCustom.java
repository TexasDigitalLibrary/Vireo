package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;

public interface UserRepoCustom {

    public User create(String email, String firstName, String lastName, Role role);

}
