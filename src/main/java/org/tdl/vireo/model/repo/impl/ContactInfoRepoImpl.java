package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.model.repo.custom.ContactInfoRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ContactInfoRepoImpl extends AbstractWeaverRepoImpl<ContactInfo, ContactInfoRepo> implements ContactInfoRepoCustom {

    @Autowired
    private ContactInfoRepo contactInfoRepo;

    @Override
    public ContactInfo create(Address address, String phone, String email) {
        return contactInfoRepo.save(new ContactInfo(address, phone, email));
    }

    @Override
    protected String getChannel() {
        return "/channel/contact-info";
    }

}
