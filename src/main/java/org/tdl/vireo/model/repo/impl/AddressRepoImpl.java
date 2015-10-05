package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.model.repo.custom.AddressRepoCustom;

public class AddressRepoImpl implements AddressRepoCustom {

    @Autowired
    private AddressRepo addressRepo;

    @Override
    public Address create(String address1, String address2, String city, String state, String postalCode, String country) {
        return addressRepo.save(new Address(address1, address2, city, state, postalCode, country));
    }
}
