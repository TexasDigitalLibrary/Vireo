vireo.controller("AdminSubmissionPreViewController", function ($location, $routeParams, SubmissionRepo) {

    SubmissionRepo.fetchSubmissionById($routeParams.id).then(function(submission) {
        $location.path("/admin/view/" + submission.id + "/" + submission.submissionWorkflowSteps[0].id);
    }).catch(function(errorMessage) {
        // handle errors
        console.log(errorMessage);
        $location.path("/admin/viewError");
    });

});
