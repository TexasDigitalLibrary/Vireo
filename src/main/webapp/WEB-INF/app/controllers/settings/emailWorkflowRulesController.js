vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();

	EmailTemplateRepo.ready().then(function() {
		$scope.newTemplate = $scope.emailTemplates[0];
	});

	$scope.openAddEmailWorkflowRuleModal = function(id) {
		$scope.recipientTypes = [		
			{type:"Submitter", data: "Submitter"},
			{type:"Assignee", data: "Assignee"},
			{type:"Organization", data: "Organization"}
		];

		console.log($scope.getSelectedOrganization());

		angular.forEach($scope.getSelectedOrganization().aggregateWorkflowSteps, function(aggregateWorkflowStep) {
			angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function(aggregateFieldProfile) {
				if(aggregateFieldProfile.inputType.name === "INPUT_CONTACT") {
					$scope.recipientTypes.push({
						type: aggregateFieldProfile.fieldGlosses[0].value,
						data: aggregateFieldProfile.fieldPredicate.id
					});
				}
			});
		});

		$scope.newRecipientType = $scope.recipientTypes[0].data;

		$scope.openModal(id);

	};

	$scope.resetEmailWorkflowRule = function() {
		$scope.newRecipient = $scope.recipients[0];
		$scope.closeModal();
	};

	$scope.setNewRecipient = function(newRecipientType) {
		$scope.newRecipientType = newRecipientType;
		console.log($scope.newRecipientType);
	};

	$scope.recipients = [];


	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready()]).then(function() {
		
		$scope.resetEmailWorkflowRule();

	});

});