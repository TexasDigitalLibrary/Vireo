package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.ContactInfo;

public interface ContactInfoRepoCustom {

    public ContactInfo create(Address address, String phone, String email);

}
