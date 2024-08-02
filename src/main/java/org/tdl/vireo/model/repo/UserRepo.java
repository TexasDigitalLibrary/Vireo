package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.user.model.IRole;

public interface UserRepo extends AbstractWeaverUserRepo<User>, UserRepoCustom {

    public User findByEmail(String email);

    public User findByNetid(String netid);

    public List<User> findAllByRoleIn(List<IRole> role, Sort sort);

    public List<User> findAllByRoleIn(List<IRole> role, Pageable pageable);

    public List<User> findAllByRoleInAndNameContainsIgnoreCase(List<IRole> role, String name, Sort sort);

    public List<User> findAllByRoleInAndNameContainsIgnoreCase(List<IRole> role, String name, Pageable pageable);

    public Page<User> findAll(Specification<User> specification, Pageable pageable);

    public List<User> findAllByActiveFilter(NamedSearchFilterGroup activeFilter);

    public List<User> findAllBySavedFilters(NamedSearchFilterGroup activeFilter);

    public Long countByRoleIn(List<IRole> role);

    public Long countByRoleInAndNameContainsIgnoreCase(List<IRole> role, String name);

}
