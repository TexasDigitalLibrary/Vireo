package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;

import play.modules.spring.Spring;

@Entity
@Table(name = "email_workflow_rule_conditions")
public class JpaEmailWorkflowRuleConditionImpl extends JpaAbstractModel<JpaEmailWorkflowRuleConditionImpl> implements AbstractWorkflowRuleCondition {

	@Column(nullable = false)
	public int displayOrder;
	
	@Column
	public Long conditionId;
		
	@Enumerated
	public ConditionType conditionType;

	public JpaEmailWorkflowRuleConditionImpl() {
		this(null);
    }
	
	public JpaEmailWorkflowRuleConditionImpl(ConditionType conditionType) {
	    this.conditionType = conditionType;
    }
	
	@Override
	public JpaEmailWorkflowRuleConditionImpl save() {
		assertManager();
		
		// make sure we have a display order in the order that we're created
		JpaEmailWorkflowRuleConditionImpl ret;
		if(this.getId() == null) {
			ret = super.save();
			ret.setDisplayOrder(Integer.parseInt(String.valueOf(ret.getId())));
		}
		ret = super.save();

		return ret;
	}

	@Override
	public JpaEmailWorkflowRuleConditionImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {

		assertManager();
		this.displayOrder = displayOrder;
	}

	@Override
	public Long getConditionId() {
		return this.conditionId;
	}

	@Override
	public void setConditionId(Long id) {
		this.conditionId = id;
	}

	@Override
	public ConditionType getConditionType() {
		return this.conditionType;
	}

	@Override
	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}


	@Override
	public String getConditionIdDisplayName() {
		String displayName = "none";
		
		if(this.conditionId == null || this.conditionType == null) {
			return displayName;
		}
		
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		 
		switch(this.conditionType) {
			case College:
				College college = settingRepo.findCollege(this.conditionId);
				if(college != null){
					displayName = college.getName();
				} else {
					displayName = "Link to College Lost!";
				}
				break;
			case Department:
				Department department = settingRepo.findDepartment(this.conditionId);
				if(department != null) {
					displayName = department.getName();
				} else {
					displayName = "Link to Department Lost!";
				}
				break;
			case Program:
				Program program = settingRepo.findProgram(this.conditionId);
				if(program != null) {
					displayName = program.getName();
				} else {
					displayName = "Link to Program Lost!";
				}
				break;
			case Always:
			default:
				displayName = "none";
				break;
		}
		
		return displayName;
	}
}
