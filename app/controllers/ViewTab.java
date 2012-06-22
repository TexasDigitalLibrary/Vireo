package controllers;

import play.mvc.Controller;

import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.modules.spring.Spring;
import play.mvc.With;

import java.text.DateFormatSymbols;

@With(Authentication.class)
public class ViewTab extends AbstractVireoController {

	@Security(RoleType.REVIEWER)
	public static void view() {		
				
		//TODO Find id by session.
		long id = 1;
		Submission submission = subRepo.findSubmission(id);
		Person submitter = submission.getSubmitter();
		
		DegreeLevel degreeLevel = settingRepo.findDegreeByName(submission.getDegree()).getLevel();
		
		String gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];
		
		String nav = "view";
		render(nav, submission, submitter, degreeLevel, gradMonth);
	}

}
