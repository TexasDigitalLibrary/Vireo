vireo.controller("EmailWorkflowRulesController", function($controller, $scope, $q, SubmissionStateRepo, EmailTemplateRepo, OrganizationRepo, EmailRecipientType, InputType) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.selectedOrganization =$scope.getSelectedOrganization();
	$scope.submissionStates = SubmissionStateRepo.getAll();
	$scope.emailTemplates = EmailTemplateRepo.getAll();
	$scope.emailRecipientType = EmailRecipientType;
	$scope.organizations = OrganizationRepo.getAll();
	$scope.stateRules = {};

	$q.all([SubmissionStateRepo.ready(), EmailTemplateRepo.ready(), OrganizationRepo.ready()]).then(function() {
		$scope.openAddEmailWorkflowRuleModal = function(id) {
			$scope.recipients = [		
				{
					label: "Submitter",
					type: EmailRecipientType.SUBMITTER, 
					data: "Submitter"
				},
				{
					label: "Assignee",
					type: EmailRecipientType.ASSIGNEE, 
					data: "Assignee"
				},
				{
					label: "Oranization",
					type: EmailRecipientType.ORGANIZATION, 
					data: null
				}
			];

			angular.forEach($scope.getSelectedOrganization().aggregateWorkflowSteps, function(aggregateWorkflowStep) {
				angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function(aggregateFieldProfile) {
					if(aggregateFieldProfile.inputType.name === InputType.INPUT_CONTACT) {
						$scope.recipients.push({
							label: aggregateFieldProfile.fieldGlosses[0].value,
							type: EmailRecipientType.CONTACT,
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

		$scope.addEmailWorkflowRule = function(newTemplate, newRecipient, submissionState) {

			var templateId = newTemplate.id;
			var recipient = angular.copy(newRecipient);
			var submissionStateId = submissionState.id;

			if(recipient.type==EmailRecipientType.ORGANIZATION) recipient.data = recipient.data.id;

			$scope.getSelectedOrganization().addEmailWorkflowRule(templateId, recipient, submissionStateId).then(function() {
				$scope.resetEmailWorkflowRule();
			});

		};

	});

});