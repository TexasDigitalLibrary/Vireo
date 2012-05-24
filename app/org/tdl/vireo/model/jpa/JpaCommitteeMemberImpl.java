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
public class JpaCommitteeMemberImpl extends JpaAbstractModel<JpaCommitteeMemberImpl> implements CommitteeMember {

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

		if (submission == null)
			throw new IllegalArgumentException("Submissions are required");
		
		if (firstName == null || firstName.length() == 0)
			throw new IllegalArgumentException("First name is required");
		
		if (lastName == null || lastName.length() == 0)
			throw new IllegalArgumentException("Last name is required");
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		this.submission = submission;
	    this.displayOrder = 0;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.chair = chair;
	}

	@Override
	public JpaCommitteeMemberImpl save() {
		
		assertReviewerOrOwner(submission.getSubmitter());

		return super.save();
	}
	
	@Override
	public JpaCommitteeMemberImpl delete() {
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		((JpaSubmissionImpl) submission).removeCommitteeMember(this);
		return super.delete();
	}
	
    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
    	
		assertReviewerOrOwner(submission.getSubmitter());
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
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(String lastName) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.lastName = lastName;
	}

	@Override
	public String getMiddleInitial() {
		return this.middleInitial;
	}

	@Override
	public void setMiddleInitial(String middleInitial) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.middleInitial = middleInitial;
	}

	@Override
	public boolean isCommitteeChair() {
		return this.chair;
	}

	@Override
	public void setCommitteeChair(boolean chair) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.chair = chair;
	}

}
