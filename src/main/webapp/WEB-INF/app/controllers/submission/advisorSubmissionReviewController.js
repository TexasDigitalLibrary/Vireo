vireo.controller("AdvisorSubmissionReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, Submission) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    var message = "You may return to this page to follow the application's progress, or provide additional input in the future.";

    $scope.messages = [];

    $scope.approval = {};

    $scope.advisorSubmissionRepoReady = false;

    AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function(submissions) {
        $scope.advisorSubmissionRepoReady = true;
        $scope.submission = submissions;
    });

    $scope.required = function(aggregateFieldProfile) {
        return !aggregateFieldProfile.optional;
    };

    $scope.predicateMatch = function(fv) {
        return function(aggregateFieldProfile) {
            return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
        };
    };

    $scope.addComment = function(approval) {
        $scope.approval.updating = true;
        $scope.submission.updateAdvisorApproval(approval).then(function(res) {

            var responseSubmission = JSON.parse(res.body).payload.Submission;

            // This should be done through a broadcast and not explicitly like this.
            $scope.submission.approveApplicationDate = responseSubmission.approveApplicationDate;
            $scope.submission.approveEmbargoDate = responseSubmission.approveEmbargoDate;

            $scope.approval.message = "";
            $scope.approval.clearApproveApplication = false;
            $scope.approval.clearApproveEmbargo = false;
            $scope.approval.approveEmbargo = undefined;
            $scope.approval.approveApplication = undefined;
            $scope.approval.updating = false;
            $scope.messages.push(message);
        });
    };

    $scope.disableCheck = function(approval) {
        var disabled = true;

        if (approval.approveEmbargo && (approval.approveApplication === undefined || approval.approveApplication)) {
            disabled = false;
        }

        if (approval.approveApplication && (approval.approveEmbargo === undefined || approval.approveEmbargo)) {
            disabled = false;
        }

        if (approval.message) {
            disabled = false;
        }

        if (approval.updating) {
            disabled = true;
        }

        return disabled;

    };

});
