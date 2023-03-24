package org.tdl.vireo.model.repo.simple;

import java.util.List;
import org.springframework.data.repository.Repository;
import org.tdl.vireo.model.simple.SimpleUser;

public interface SimpleUserRepo extends Repository<SimpleUser, Long> {

    public SimpleUser findByEmail(String email);

    public List<SimpleUser> findAll();

    public SimpleUser findById(Long id);
}
