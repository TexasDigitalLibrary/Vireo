package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Set;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SearchDirection;
import org.tdl.vireo.model.SearchFilter;
import org.tdl.vireo.model.SearchOrder;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.state.StateManager;

/**
 * Jpa specific implementation of the Vireo Submission Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSubmissionRepositoryImpl implements SubmissionRepository {
	
	// //////////////////
	// Submission Model
	// //////////////////
	
	@Override
	public Submission createSubmission(Person submitter) {
		return new JpaSubmissionImpl(submitter);
	}

	@Override
	public Submission findSubmission(Long id) {
		return (Submission) JpaSubmissionImpl.findById(id);
	}

	@Override
	public Submission findSubmissionByEmailHash(String emailHash) {
		return JpaSubmissionImpl.find("committeeEmailHash = ?", emailHash).first();
	}

	@Override
	public List<Submission> findSubmission(Person submitter) {
		return JpaSubmissionImpl.find("submitter = ?", submitter).fetch();

	}
	
	@Override
	public List<Submission> filterSearchSubmissions(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		// TODO Implement this method.
		return null;
	}

	// //////////////////////////////////////////////////////////////
	// Attachment, Committee Member, and Custom Action Value Models
	// //////////////////////////////////////////////////////////////
	
	@Override
	public Attachment findAttachment(Long id) {
		return (Attachment) JpaAttachmentImpl.findById(id);
	}

	@Override
	public CommitteeMember findCommitteeMember(Long id) {
		return (CommitteeMember) JpaCommitteeMemberImpl.findById(id);

	}

	@Override
	public CustomActionValue findCustomActionValue(Long id) {
		return (CustomActionValue) JpaCustomActionValueImpl.findById(id);
	}

	// //////////////////
	// Action Log Model
	// //////////////////
	
	@Override
	public ActionLog findActionLog(Long id) {
		return (ActionLog) JpaActionLogImpl.findById(id);
	}

	@Override
	public List<ActionLog> findActionLog(Submission submission) {
		return JpaActionLogImpl.find("submission = ? order by actionDate", submission).fetch();
	}

	@Override
	public List<ActionLog> filterSearchActionLogs(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		// TODO Implement this method.
		return null;
	}
}
