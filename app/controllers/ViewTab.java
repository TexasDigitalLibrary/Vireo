package controllers;

import play.mvc.Controller;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.modules.spring.Spring;
import play.mvc.With;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * The controller for the view tab.
 * 
 * @author Micah Cooper
 *
 */

@With(Authentication.class)
public class ViewTab extends AbstractVireoController {

	@Security(RoleType.REVIEWER)
	public static void view() {		
				
		if(params.get("subId") != null){
			session.put("submission", params.get("subId"));
		}
		
		//TODO Remove this line.
		//session.put("submission", "1");
		
		Long id = null;
		if(session.contains("submission")){
			id = Long.valueOf(session.get("submission"));
		} else {
			FilterTab.list();
		}
		
		Submission submission = subRepo.findSubmission(id);
		Person submitter = submission.getSubmitter();
		
		DegreeLevel degreeLevel = settingRepo.findDegreeByName(submission.getDegree()).getLevel();
		
		String gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];
		
		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);		
		
		String nav = "view";
		render(nav, submission, submitter, degreeLevel, gradMonth, actionLogs);
	}

}
