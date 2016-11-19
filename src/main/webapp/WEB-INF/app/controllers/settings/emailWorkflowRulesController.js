vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo, OrganizationRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();
	$scope.organizations = OrganizationRepo.getAll();


	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready(), OrganizationRepo.ready()]).then(function() {

		$scope.openAddEmailWorkflowRuleModal = function(id) {
			$scope.recipientTypes = [		
				{type:"Submitter", data: "Submitter"},
				{type:"Assignee", data: "Assignee"},
				{type:"Organization", data: "Organization"}
			];

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

			console.log($scope.organizations);

			$scope.openModal(id);

		};

		$scope.resetEmailWorkflowRule = function() {
			$scope.newRecipientType = $scope.recipientTypes[0].data;
			$scope.closeModal();
		};

		$scope.setNewRecipient = function(newRecipientType) {
			$scope.newRecipientType = newRecipientType;
			console.log($scope.newRecipientType);
		};

		$scope.newTemplate = $scope.emailTemplates[0];

	});

});