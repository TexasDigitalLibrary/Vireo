package org.tdl.vireo.model.impl;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Submission;

/**
 *
 * @author <a href="http://www.sandsfish.com">Sands Fish</a>
 */
public class CommitteeMemberImpl implements CommitteeMember {

    private String firstName;
    private String lastName;
    private String middleName;
    private boolean chair;
    private int displayOrder;
    
    public CommitteeMemberImpl() {
    }
            
    public CommitteeMemberImpl(String firstName, String lastName, String middleName, boolean chair) {
        this.setFirstName(firstName);
        this.setMiddleName(middleName);
        this.setLastName(lastName);
        this.setCommitteeChair(chair);
    }

    public Submission getSubmission() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFullName() {
        if (middleName == null) {
            return firstName + " " + lastName;
        } else {
            return firstName + " " + middleName + " " + lastName;
        }
    }

    public boolean isCommitteeChair() {
        return this.chair;
    }

    public void setCommitteeChair(boolean chair) {
        this.chair = chair;
    }

    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends AbstractModel> T save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends AbstractModel> T delete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends AbstractModel> T refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends AbstractModel> T merge() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public <T extends AbstractModel> T detach() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}