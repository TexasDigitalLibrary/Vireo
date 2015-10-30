package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public interface UserRepo extends JpaRepository<User, Long>, UserRepoCustom {

    public User findByEmail(String email);

}
