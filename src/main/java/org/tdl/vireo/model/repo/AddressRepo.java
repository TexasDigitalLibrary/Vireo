package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.repo.custom.AddressRepoCustom;

public interface AddressRepo extends JpaRepository<Address, Long>, AddressRepoCustom {

}
