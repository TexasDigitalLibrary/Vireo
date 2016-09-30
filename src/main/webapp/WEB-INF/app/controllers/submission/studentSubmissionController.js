vireo.controller("StudentSubmissionController", function ($controller, $scope, $routeParams, StudentSubmissionRepo, Submission) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {
		console.log(angular.fromJson(data.body).payload);
		$scope.submission = new Submission(angular.fromJson(data.body).payload.Submission);
		$scope.setAcitveStep($scope.submission.submissionWorkflowSteps[0]);
	});

	$scope.setAcitveStep = function(step) {
		$scope.activeStep = step;
	};

});