vireo.controller("AdvisorReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, AdvisorSubmission, Submission) {

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
        $scope.updatingApproval = true;
        console.log('add comment')
        $scope.submission.updateAdvisorApproval(approval).then(function(res) {
            $scope.submission = new Submission(angular.fromJson(res.body).payload.Submission);
            $scope.approval.message = "";
            $scope.approval.clearApproveApplication = false;
            $scope.approval.clearApproveEmbargo = false
            $scope.approval.approveEmbargo = undefined;
            $scope.approval.approveApplication = undefined;
            $scope.updatingApproval = false;
            console.log('add message')
            $scope.messages.push(message);
            console.log($scope.messages)
        });
    };

    $scope.disableCheck = function(approval) {
        var dissabled = true;
        //((approval.approveEmbargo===undefined&&approval.approveApplication===undefined)&&!approval.message)||(approval.approveApplication===false&&!approval.message)||(approval.approveEmbargo===false&&!approval.message)

        if (approval.approveEmbargo && (approval.approveApplication === undefined || approval.approveApplication)) {
            dissabled = false;
        }

        if (approval.approveApplication && (approval.approveEmbargo === undefined || approval.approveEmbargo)) {
            dissabled = false;
        }

        if (approval.message) {
            dissabled = false;
        }

        return dissabled;

    }

});
