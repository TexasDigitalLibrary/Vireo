vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();

	EmailTemplateRepo.ready().then(function() {
		$scope.newTemplate = $scope.emailTemplates[0];
	});

	$scope.addRule = function() {
		console.log("Add rule");
	};

	$scope.resetEmailWorkflowRule = function() {
		$scope.closeModal();
	};

	$scope.recipients = [];


	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready()]).then(function() {



	});

});