package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class ContactInfo extends BaseEntity {

    @OneToOne(cascade = {DETACH, MERGE, REFRESH, REMOVE}, optional = true, orphanRemoval = true, fetch = LAZY)
    private Address address;
    
    @Column(nullable = true)
    private String phone;
    
    @Column(nullable = true)
    private String email;

    public ContactInfo() { }

    public ContactInfo(Address address, String phone, String email) {
        setAddress(address);
        setPhone(phone);
        setEmail(email);
    }

    /**
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
