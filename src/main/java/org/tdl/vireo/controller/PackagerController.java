package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.util.PackagerUtility;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/packager")
public class PackagerController {

    @Autowired
    private AbstractPackagerRepo packagerRepo;

    // TODO: remove submission repo
    @Autowired
    private SubmissionRepo submissionRepo;

    // TODO remove packager utility
    @Autowired
    private PackagerUtility packagerUtility;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allPackagers() {
        return new ApiResponse(SUCCESS, packagerRepo.findAll());
    }

    // TODO: remove test endpoint
    @ApiMapping("/test/{packagerId}/{submissionId}")
    @Auth(role = "MANAGER")
    public ApiResponse test(@ApiVariable Long packagerId, @ApiVariable Long submissionId) throws Exception {
        packagerUtility.packageExport(packagerRepo.findOne(packagerId), submissionRepo.findOne(submissionId));        
        return new ApiResponse(SUCCESS);
    }

}
