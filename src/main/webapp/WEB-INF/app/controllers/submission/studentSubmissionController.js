vireo.controller("StudentSubmissionController", function ($controller, $scope, $location, $routeParams, $anchorScroll, $timeout, StudentSubmissionRepo, Submission, SubmissionStateRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SubmissionStateRepo.ready().then(function(){
		$scope.submittedSubmissionState = SubmissionStateRepo.findByName('Submitted');
	});

	$scope.onLastStep = function() {
		return true;
	};
	
	$scope.studentSubmissionRepoReady = false;

	StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {
  		$timeout(function() {
            $anchorScroll();
        });
		$scope.studentSubmissionRepoReady = true;
		$scope.submission = new Submission(angular.fromJson(data.body).payload.Submission);

		$scope.onLastStep = function() {
			var currentStepIndex = $scope.submission.submissionWorkflowSteps.indexOf($scope.nextStep);
			return currentStepIndex === -1;
		};

		var currentStep = $routeParams.stepNum?$scope.submission.submissionWorkflowSteps[$routeParams.stepNum-1]:$scope.submission.submissionWorkflowSteps[0];

		$scope.setActiveStep(currentStep);

	});

	$scope.setActiveStep = function(step) {

		var stepIndex = $scope.submission.submissionWorkflowSteps.indexOf(step);
		var reviewStepNum = $scope.submission.submissionWorkflowSteps.length+1;
		var stepNum = stepIndex+1;
		
		if(!step) {
			if(parseInt($routeParams.stepNum) === reviewStepNum) {
				step = {name: "review"};
				stepNum = reviewStepNum;
			} else {
				stepIndex = 0;
				stepNum = stepIndex+1;
				step = $scope.submission.submissionWorkflowSteps[stepIndex];
			}
		} else if(step.name === "review") {
			stepNum = reviewStepNum;
		}

		$scope.nextStep = $scope.submission.submissionWorkflowSteps[stepNum];
		$scope.activeStep = step;

		$location.path("submission/"+$scope.submission.id+"/step/"+stepNum, false);

	};

	$scope.submit = function() {
	  $scope.submission.changeStatus($scope.submittedSubmissionState);
	};

});
