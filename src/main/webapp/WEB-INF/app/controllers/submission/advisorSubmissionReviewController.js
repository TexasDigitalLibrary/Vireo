vireo.controller("AdvisorSubmissionReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, AdvisorSubmission, Submission) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    var message = "You may return to this page to follow the application's progress, or provide additional input in the future.";

    $scope.messages = [];

    $scope.approval = {};

    $scope.advisorSubmissionRepoReady = false;

    AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function(data) {
        $scope.advisorSubmissionRepoReady = true;
        $scope.submission = new AdvisorSubmission(angular.fromJson(data.body).payload.Submission);
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
            angular.extend($scope.submission, angular.fromJson(res.body).payload.Submission);
            $scope.approval.message = "";
            $scope.approval.clearApproveApplication = false;
            $scope.approval.clearApproveEmbargo = false
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

    }

});
