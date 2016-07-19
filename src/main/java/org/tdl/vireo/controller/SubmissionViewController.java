package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionViewColumn;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission-view")
public class SubmissionViewController {
    
    @ApiMapping("/all-columns")
    @Auth(role = "STUDENT")
    public ApiResponse getSubmissionViewColumns() {
        List<SubmissionViewColumn> columns =  new ArrayList<SubmissionViewColumn>(Arrays.asList(new SubmissionViewColumn[] {
            new SubmissionViewColumn("ID", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Student name", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Status", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Assigned to", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document title", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Submission date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Approval date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Embargo type", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Student email", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Institutional ID", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document keywords", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document abstract", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document subjects", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Published material", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document language", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Graduation semester", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Defense date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Primary document", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("License agreement date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Committee approval date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Committee embargo approval date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Committee members", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Committee contact email", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Degree", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Degree level", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Program", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("College", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Department", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Major", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document type", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("UMI release", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Custom actions", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Deposit ID", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Notes", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Last event", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Event time", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("ORCID", Sort.NONE, new String[] {})
        }));
        return new ApiResponse(SUCCESS, columns);
    }
    
    @ApiMapping("/columns-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getSubmissionViewColumnsByUser(@ApiCredentials Credentials credentials) {
        List<SubmissionViewColumn> columns =  new ArrayList<SubmissionViewColumn>(Arrays.asList(new SubmissionViewColumn[] {
            new SubmissionViewColumn("ID", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Student name", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Status", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Assigned to", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Document title", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Submission date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Approval date", Sort.NONE, new String[] {}),
            new SubmissionViewColumn("Embargo type", Sort.NONE, new String[] {})
        }));
        return new ApiResponse(SUCCESS, columns);
    }

}
