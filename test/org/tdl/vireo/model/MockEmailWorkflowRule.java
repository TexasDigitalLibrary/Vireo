package org.tdl.vireo.model;

import java.util.List;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.state.State;

/**
 * This is a simple mock email template class that may be useful for testing. Feel free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock object and then set whatever relevant properties are needed for your particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockEmailWorkflowRule extends AbstractMock implements EmailWorkflowRule {

	/* Email Workflow Rule properties */
	private int displayOrder;
	private State state;
	private AbstractWorkflowRuleCondition condition;
	private boolean isSystem = false;
	private boolean isDisabled = true;
	private RecipientType recipientType;
	private AdministrativeGroup adminGroupRecipient;
	private EmailTemplate emailTemplate;

	@Override
	public MockEmailWorkflowRule save() {
		return this;
	}

	@Override
	public MockEmailWorkflowRule delete() {
		return this;
	}

	@Override
	public MockEmailWorkflowRule refresh() {
		return this;
	}

	@Override
	public MockEmailWorkflowRule merge() {
		return this;
	}

	@Override
	public MockEmailWorkflowRule detach() {
		return this;
	}

	@Override
	public State getAssociatedState() {
		return state;
	}

	@Override
	public void setAssociatedState(State state) {
		this.state = state;
	}

	@Override
	public AbstractWorkflowRuleCondition getCondition() {
		return condition;
	}

	@Override
	public void setCondition(AbstractWorkflowRuleCondition condition) {
		this.condition = condition;
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
	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	@Override
	public boolean isSystem() {
		return isSystem;
	}

	@Override
	public void disable() {
		this.isDisabled = true;
	}

	@Override
	public void enable() {
		this.isDisabled = false;
	}

	@Override
	public boolean isDisabled() {
		return isDisabled;
	}

	@Override
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	@Override
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@Override
	public List<String> getRecipients(Submission submission) {
		return null;
	}

	@Override
	public RecipientType getRecipientType() {
		return recipientType;
	}

	@Override
	public void setAdminGroupRecipient(AdministrativeGroup adminGroup) {
		this.adminGroupRecipient = adminGroup;
	}

	@Override
	public AdministrativeGroup getAdminGroupRecipient() {
		return adminGroupRecipient;
	}

	@Override
	public void setRecipientType(RecipientType recipientType) {
		this.recipientType = recipientType;
	}
}
