vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo, OrganizationRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();
	$scope.organizations = OrganizationRepo.getAll();


	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready(), OrganizationRepo.ready()]).then(function() {

		$scope.openAddEmailWorkflowRuleModal = function(id) {
			$scope.recipients = [		
				{type:"Submitter", data: "Submitter"},
				{type:"Assignee", data: "Assignee"},
				{type:"Organization", data: null}
			];

			angular.forEach($scope.getSelectedOrganization().aggregateWorkflowSteps, function(aggregateWorkflowStep) {
				angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function(aggregateFieldProfile) {
					if(aggregateFieldProfile.inputType.name === "INPUT_CONTACT") {
						$scope.recipients.push({
							type: aggregateFieldProfile.fieldGlosses[0].value,
							data: aggregateFieldProfile.fieldPredicate.id
						});
					}
				});
			});

			$scope.newTemplate = $scope.emailTemplates[0];
			$scope.newRecipient = $scope.recipients[0];

			$scope.openModal(id);

		};

		$scope.resetEmailWorkflowRule = function() {
			$scope.newTemplate = $scope.emailTemplates[0];
			$scope.newRecipient = $scope.recipients[0].data;
			$scope.closeModal();
		};

		$scope.setNewRecipient = function(newRecipient) {			
			console.log($scope.newRecipient);
		};

		$scope.addEmailWorkflowRule = function(newTemplate, newRecipient) {

			var organizationId = $scope.getSelectedOrganization().id;
			var templateId = newTemplate.id;
			var recipient = angular.copy(newRecipient);

			if(recipient.type=="Organization") recipient.data = recipient.data.id;

			console.log($scope.getSelectedOrganization(), newTemplate, recipient);
		};

	});

});