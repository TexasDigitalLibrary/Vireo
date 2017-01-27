vireo.controller("AdvisorReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, AdvisorSubmission, InputTypes) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.InputType = InputTypes;

	AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function(data) {
		$scope.submission = new AdvisorSubmission(angular.fromJson(data.body).payload.Submission);
	});

	$scope.required = function(aggregateFieldProfile) {
		return !aggregateFieldProfile.optional;
	};

	$scope.predicateMatch = function(fv) {
		return function(aggregateFieldProfile) {
	        return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
	    }
	};

});	