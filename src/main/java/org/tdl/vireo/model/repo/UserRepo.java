package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.user.model.IRole;

public interface UserRepo extends AbstractWeaverUserRepo<User>, UserRepoCustom {

    public User findByEmail(String email);

    public List<User> findAllByRole(IRole role);

}
