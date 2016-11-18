vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();

	EmailTemplateRepo.ready().then(function() {
		$scope.newTemplate = $scope.emailTemplates[0];
	});

	$scope.openAddEmailWorkflowRuleModal = function(id) {
		$scope.recipientTypes = [		
			"Submitter",
			"Assignee",
			"Organization"
		];

		console.log($scope.getSelectedOrganization());

		angular.forEach($scope.getSelectedOrganization().aggregateWorkflowSteps, function(aggregateWorkflowStep) {
			angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function(aggregateFieldProfile) {
				if(aggregateFieldProfile.inputType.name === "INPUT_CONTACT") {
					console.log(aggregateFieldProfile);
					$scope.recipientTypes.push(aggregateFieldProfile.fieldGlosses[0].value);
				}
			});
		});

		$scope.newRecipientType = $scope.recipientTypes[0];

		$scope.openModal(id);

	}

	$scope.addEmailWorkflowRule = function() {
		console.log("Add rule");
	};

	$scope.resetEmailWorkflowRule = function() {
		$scope.newRecipient = $scope.recipients[0];
		$scope.closeModal();
	};

	$scope.recipients = [];


	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready()]).then(function() {
		
		$scope.resetEmailWorkflowRule();

	});

});