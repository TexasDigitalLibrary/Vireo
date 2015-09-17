package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's CommitteeMember interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "committee_member")
public class JpaCommitteeMemberImpl extends
		JpaAbstractModel<JpaCommitteeMemberImpl> implements CommitteeMember {

	@Column(nullable = false)
	public int displayOrder;

	@ManyToOne(targetEntity = JpaSubmissionImpl.class, optional = false)
	public Submission submission;

	@Column(length = 255)
	public String firstName;

	@Column(length = 255)
	public String lastName;

	@Column(length = 255)
	public String middleName;

	@ElementCollection
	@OrderColumn
	@CollectionTable(name = "committee_member_roles")
	public List<String> roles;

	
	/**
	 * Create a new JpaCommitteeMemberImpl
	 * 
	 * @param submission
	 *            The submission this member belongs too.
	 * @param firstName
	 *            The first name of the member.
	 * @param lastName
	 *            The last name of the member.
	 * @param middleName
	 *            The middle name of the member.
	 */
	protected JpaCommitteeMemberImpl(Submission submission, String firstName, String lastName, String middleName) {

		if (submission == null)
			throw new IllegalArgumentException("Submissions are required");

		if (firstName != null && firstName.trim().length() == 0)
			firstName = null;

		if (lastName != null && lastName.trim().length() == 0)
			lastName = null;

		if (firstName == null && lastName == null)
			throw new IllegalArgumentException(
					"Either a first or a last name is required.");

		assertReviewerOrOwner(submission.getSubmitter());

		this.submission = submission;
		this.displayOrder = 0;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.roles = new ArrayList<String>();
	}

	@Override
	public JpaCommitteeMemberImpl save() {

		assertReviewerOrOwner(submission.getSubmitter());

		// Do the check to ensure that there it at least a first or a last name
		// available.
		if ((firstName == null || firstName.length() == 0)
				&& (lastName == null || lastName.length() == 0))
			throw new IllegalArgumentException(
					"Either a first or a last name is required.");

		boolean newObject = false;
		if (id == null)
			newObject = true;

		super.save();

		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {
			if (newObject) {

				// We're a new object so log the addition.
				String entry = _generateLogEntry("added");
				submission.logAction(entry).save();

			} else {

				// We've been updated so log the change.
				String entry = _generateLogEntry("modified");
				submission.logAction(entry).save();
			}
		}

		return this;
	}

	@Override
	public JpaCommitteeMemberImpl delete() {

		String entry = _generateLogEntry("removed");

		// Clear out relations to this object.
		assertReviewerOrOwner(submission.getSubmitter());
		((JpaSubmissionImpl) submission).removeCommitteeMember(this);
		this.roles.clear();
		
		super.delete();

		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {
			submission.logAction(entry).save();
		}

		return this;
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
	public String getMiddleName() {
		return this.middleName;
	}

	@Override
	public void setMiddleName(String middleName) {

		assertReviewerOrOwner(submission.getSubmitter());
		this.middleName = middleName;
	}

	@Override
	public String getFormattedName(NameFormat format) {

		return NameFormat.format(format, firstName, middleName, lastName, null);
	}

	@Override
	public List<String> getRoles() {
		return this.roles;
	}

	@Override
	public void addRole(String role) {
		if (role == null)
			throw new IllegalArgumentException(
					"Unable to add null role to committee member");

		if (this.roles.contains(role))
			throw new IllegalArgumentException("The role '" + role
					+ "' already exists for committee member");

		assertReviewerOrOwner(submission.getSubmitter());

		roles.add(role);
	}

	@Override
	public void removeRole(String role) {

		if (role == null)
			throw new IllegalArgumentException(
					"Unable to remove null role to committee member");

		assertReviewerOrOwner(submission.getSubmitter());

		roles.remove(role);
	}
	
	@Override
	public boolean hasRole(String ... roles) {
		
		for (String role : roles) {
			if (this.roles.contains(role))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNoRole() {
		return this.roles.size() == 0;
	}

	@Override
	public String getFormattedRoles() {

		SettingsRepository settingRepo = Spring
				.getBeanOfType(SettingsRepository.class);
		List<CommitteeMemberRoleType> orderedTypes = settingRepo
				.findAllCommitteeMemberRoleTypes(this.submission
						.getDegreeLevel());

		StringBuilder result = new StringBuilder();

		// Build the result based upon the ordered types.
		List<String> rolesCopy = new ArrayList<String>(this.roles);
		for (CommitteeMemberRoleType type : orderedTypes) {
			if (rolesCopy.contains(type.getName())) {
				if (result.length() != 0)
					result.append(", ");
				result.append(type.getName());

				rolesCopy.remove(type.getName());
			}
		}

		// Anything else just get's added to the end of the string.
		for (String role : rolesCopy) {
			if (result.length() != 0)
				result.append(", ");
			result.append(role);
		}

		return result.toString();
	}

	/**
	 * Private utility method to generate the text of a log entry. It will
	 * prepare the entry as:
	 * 
	 * Committee Member 'So-and-So' as Role 1, Role 2, Role 3 [action]
	 * 
	 * @param action
	 *            The action being taken, added, modified, removed.
	 * @return The full text string of the entry.
	 */
	private String _generateLogEntry(String action) {

		String entry = "Committee member '"+ this.getFormattedName(NameFormat.FIRST_MIDDLE_LAST) + "'";
		if (this.roles.size() > 0)
			entry += " ("+getFormattedRoles()+")";
		entry += " " + action;

		return entry;

	}

}
