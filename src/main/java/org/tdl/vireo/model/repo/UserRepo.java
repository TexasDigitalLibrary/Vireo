package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public interface UserRepo extends JpaRepository<User, Long>, UserRepoCustom {

    public User findByEmail(String email);

    public List<User> findByActiveFilter(NamedSearchFilter activeFilter);

    public void delete(User user);

}
