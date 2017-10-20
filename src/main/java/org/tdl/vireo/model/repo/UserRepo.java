package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface UserRepo extends WeaverRepo<User>, UserRepoCustom {

    public User findByEmail(String email);

}
