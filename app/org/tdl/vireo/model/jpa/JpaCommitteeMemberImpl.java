package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Submission;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's CommitteeMember interface
 * 
 * TODO: Create actionLog items when the submission is changed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "CommitteeMember")
public class JpaCommitteeMemberImpl extends Model implements CommitteeMember {

	@Column(nullable = false)
	public int displayOrder;

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	public String firstName;
	public String lastName;
	public String middleInitial;
	public boolean chair;

	/**
	 * Create a new JpaCommitteeMemberImpl
	 * 
	 * @param submission
	 *            The submission this member belongs too.
	 * @param firstName
	 *            The first name of the member.
	 * @param lastName
	 *            The last name of the member.
	 * @param middleInitial
	 *            The middle initial of the member.
	 * @param chair
	 *            Weather this member is a chair or co-chair.
	 */
	protected JpaCommitteeMemberImpl(Submission submission, String firstName,
			String lastName, String middleInitial, boolean chair) {

		// TODO: Check that the arguments are valid.

	    this.displayOrder = 0;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.chair = chair;
	}

	@Override
	public JpaCommitteeMemberImpl save() {
		return super.save();
	}

	@Override
	public JpaCommitteeMemberImpl delete() {
		
		// TODO: Call back to submission and tell it that this member is being deleted.
		
		return super.delete();
	}

	@Override
	public JpaCommitteeMemberImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaCommitteeMemberImpl merge() {
		return super.merge();
	}

    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

	@Override
	public Submission getSubmission() {
		return this.submission;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getMiddleInitial() {
		return this.middleInitial;
	}

	@Override
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	@Override
	public boolean isCommitteeChair() {
		return this.chair;
	}

	@Override
	public void setCommitteeChair(boolean chair) {
		this.chair = chair;
	}

}
