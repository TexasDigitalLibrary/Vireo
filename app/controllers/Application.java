package controllers;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import play.*;
import play.mvc.*;
import play.mvc.Http.Header;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import org.tdl.vireo.model.RoleType;

@With(Authentication.class)
public class Application extends AbstractVireoController {

    public static void index() {

        // Check to see if they have active submissions
    	
        Person submitter = context.getPerson();
        List<Submission> submissionList = subRepo.findSubmission(submitter);

        if(submissionList.size() > 0) {
            render(submissionList);
        }
        
        render();
    }
        
     

}