vireo.controller("AdvisorSubmissionReviewController", function ($controller, $scope, $routeParams, AdvisorSubmissionRepo, Submission) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    var message = "You may return to this page to follow the application's progress, or provide additional input in the future.";

    $scope.messages = [];

    $scope.approval = {};

    $scope.advisorSubmissionRepoReady = false;

    AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function (submissions) {
        $scope.advisorSubmissionRepoReady = true;
        $scope.submission = submissions;
        resetApproveProxy();
        $scope.submission.fetchDocumentTypeFileInfo();
    });

    var getApproveStatus = function (statusName) {
        if ($scope.submission[statusName+"Date"]) {
            return $scope.submission[statusName];
        }
        return null;
    };

    var resetApproveProxy = function () {
        $scope.approval.message = "";
        $scope.approval.embargo = {"clearApproval":false,"approve":getApproveStatus("approveEmbargo")};
        $scope.approval.advisor = {"clearApproval":false,"approve":getApproveStatus("approveAdvisor")};
        $scope.approval.updating = false;
    };

    $scope.required = function (aggregateFieldProfile) {
        return !aggregateFieldProfile.optional;
    };

    $scope.predicateMatch = function (fv) {
        return function (aggregateFieldProfile) {
            return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
        };
    };

    $scope.addComment = function () {
        $scope.approval.updating = true;
        $scope.submission.updateAdvisorApproval($scope.approval).then(function (res) {
        var responseSubmission = angular.fromJson(res.body).payload.Submission;

        // This should be done through a broadcast and not explicitly like this.
        $scope.submission.approveAdvisorDate = responseSubmission.approveAdvisorDate;
        $scope.submission.approveEmbargoDate = responseSubmission.approveEmbargoDate;
        $scope.submission.approveAdvisor = responseSubmission.approveAdvisor;
        $scope.submission.approveEmbargo = responseSubmission.approveEmbargo;

        resetApproveProxy();
        $scope.messages.push(message);
        });
    };

    $scope.disableCheck = function (approval) {
        var disabled = true;
        if (approval.embargo !== undefined && approval.advisor !== undefined) {
            if (approval.embargo.approve && (approval.advisor.approve === undefined || approval.advisor.approve)) {
                disabled = false;
            }

            if (approval.advisor.approve && (approval.embargo.approve === undefined || approval.embargo.approve)) {
                disabled = false;
            }

            if (approval.message) {
                disabled = false;
            }

            if (approval.updating) {
                disabled = true;
            }
        }
        return disabled;

    };

});
