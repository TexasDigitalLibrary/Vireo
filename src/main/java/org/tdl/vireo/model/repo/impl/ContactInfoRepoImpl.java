package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.model.repo.custom.ContactInfoRepoCustom;

public class ContactInfoRepoImpl implements ContactInfoRepoCustom {

    @Autowired
    private ContactInfoRepo contactInfoRepo;

    @Override
    public ContactInfo create(Address address, String phone, String email) {
        return contactInfoRepo.save(new ContactInfo(address, phone, email));
    }

}
