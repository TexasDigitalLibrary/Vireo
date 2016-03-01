package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 
 * @author gad
 */
@Entity
public class CommitteeMember extends BaseEntity {

    @Column(length = 255, nullable = false)
    private String firstName;

    @Column(length = 255, nullable = false)
    private String lastName;

    @Column(length = 255, nullable = true)
    private String middleName;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    /**
     * 
     */
    public CommitteeMember() {
        
    }
    
    /**
     * 
     * @param firstName
     * @param lastName
     * @param email
     */
    public CommitteeMember(String firstName, String lastName, String email) {
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
