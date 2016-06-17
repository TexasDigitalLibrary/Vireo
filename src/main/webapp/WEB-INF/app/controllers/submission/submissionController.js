vireo.controller("SubmissionController", function($controller, $scope, $routeParams, SubmissionRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	SubmissionRepo.findById($routeParams.submissionId).then(function(submission) {
		$scope.submission = submission;
		$scope.setAcitveStep($scope.submission.submissionWorkflowSteps[0]);
	});

	$scope.setAcitveStep = function(step) {
		$scope.activeStep = step;
	};

});