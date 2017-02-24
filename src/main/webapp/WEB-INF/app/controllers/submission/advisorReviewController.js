vireo.controller("AdvisorReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, AdvisorSubmission, InputTypes) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.InputType = InputTypes;

    $scope.approval = {};

	$scope.advisorSubmissionRepoReady = false;
	AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function(data) {
		$scope.advisorSubmissionRepoReady = true;
		$scope.submission = new AdvisorSubmission(angular.fromJson(data.body).payload.Submission);
        console.log($scope.submission);
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
        $scope.submission.updateAdvisorApproval(approval).then(function() {
            $scope.approval.message="";
            $scope.approval.clearApproveApplication=false;
            $scope.approval.clearApproveEmbargo=false;
            $scope.updatingApproval = false;
        });
    };

});
