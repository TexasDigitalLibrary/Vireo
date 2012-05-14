package org.tdl.vireo.model.jpa;

import java.util.Date;

import javax.naming.OperationNotSupportedException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import com.sun.xml.internal.txw2.IllegalAnnotationException;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's Action log.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "ActionLog")
public class JpaActionLogImpl extends Model implements ActionLog {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional = false)
	public Submission submission;

	@Column(nullable = false)
	public String submissionState;

	@ManyToOne(targetEntity=JpaPersonImpl.class, optional = false)
	public Person person;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date when;

	@ManyToOne(targetEntity=JpaAttachmentImpl.class)
	public Attachment attachment;

	@Column(nullable = false)
	public String entry;

	@Column(nullable = false)
	public boolean privateFlag;

	/**
	 * Create a new JpaActionLogImpl.
	 * 
	 * @param submission
	 *            The submission this action log affects.
	 * @param submissionState
	 *            The state of the submission after the action log.
	 * @param person
	 *            The person who made the change.
	 * @param when
	 *            When the action occured.
	 * @param attachment
	 *            The attachment affected, may be null.
	 * @param entry
	 *            An english description of the action.
	 * @param privateFlag
	 *            Weather the action should be published publicly viewable.
	 */
	protected JpaActionLogImpl(Submission submission, State submissionState,
			Person person, Date when, Attachment attachment, String entry,
			boolean privateFlag) {

		// TODO: Check that all the parameters are not null, good, etc...

		this.submission = submission;
		this.submissionState = submissionState.getBeanName();
		this.person = person;
		this.when = when;
		this.attachment = attachment;
		this.entry = entry;
		this.privateFlag = privateFlag;
	}

	@Override
	public JpaActionLogImpl save() {
		return super.save();
	}

	@Override
	public JpaActionLogImpl delete() {
		// You can not delete action logs... at least not right now.
		throw new IllegalStateException("Action Logs may not be deleted.");
		// return super.delete();
	}

	@Override
	public JpaActionLogImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaActionLogImpl merge() {
		return super.merge();
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public State getSubmissionState() {
		return Spring.getBeanOfType(StateManager.class).getState(submissionState);
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public Date getWhen() {
		return when;
	}

	@Override
	public Attachment getAttachment() {
		return attachment;
	}

	@Override
	public String getEntry() {
		return entry;
	}

	@Override
	public boolean isPrivate() {
		return privateFlag;
	}

	@Override
	public void setPrivate(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}
}
