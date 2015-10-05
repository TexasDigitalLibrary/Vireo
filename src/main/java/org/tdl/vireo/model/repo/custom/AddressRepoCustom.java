package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Address;

public interface AddressRepoCustom {
    public Address create(String address1, String address2, String city, String state, String postalCode, String country);
}
