package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;

public interface UserRepo extends AbstractWeaverUserRepo<User>, UserRepoCustom {

    public User findByEmail(String email);

}
