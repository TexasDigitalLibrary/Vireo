package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.repo.custom.AddressRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface AddressRepo extends WeaverRepo<Address>, AddressRepoCustom {

}
