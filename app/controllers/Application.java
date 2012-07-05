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
        dumpRequestHeaders();
        dumpConfiguration();


        // Check to see if they have active submissions
        Person submitter = context.getPerson();
        List<Submission> submissionList = subRepo.findSubmission(submitter);

        if(submissionList.size() > 0) {
            Submission submission = submissionList.get(0);
            String documentTitle = submission.getDocumentTitle();

            Attachment primaryDocument = submission.getPrimaryDocument();
            String primaryDocumentName = primaryDocument.getName();
            URI primaryDocumentFileURI = primaryDocument.getFile().toURI();

            State state = submission.getState();
            String stateDisplayName = state.getDisplayName();

            Date date = submission.getSubmissionDate();
            String dateString = date.toString();

            String assigneeDisplayName = submission.getAssignee().getDisplayName();

            Boolean editableByStudent = state.isEditableByStudent();
            Long id = submission.getId();

            render("Submit/submissionStatus.html",
                    documentTitle,
                    primaryDocumentName,
                    primaryDocumentFileURI,
                    stateDisplayName,
                    dateString,
                    assigneeDisplayName,
                    editableByStudent,
                    id);
        }
        render();
    }
        
     
    private static void dumpConfiguration(){
	Logger.info(play.Play.configuration.toString());
    }
    
    private static void dumpRequestHeaders() {
       	Logger.info("Headers ------------------");
       	Logger.info(request.toString());
       	Logger.info(session.toString());
       	Map<String, Header> rsp = request.headers;
    	for (Map.Entry<String, Header> entry : rsp.entrySet())    	{
    	    Logger.info(entry.getKey() + "= {" + entry.getValue() + "}");
    	}
    }
}