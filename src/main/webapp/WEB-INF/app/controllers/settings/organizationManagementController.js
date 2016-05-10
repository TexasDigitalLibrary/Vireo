vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, OrganizationRepo, OrganizationCategoryRepo, WorkflowStepRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationCategories = OrganizationCategoryRepo.get();

	$scope.currentWorkflowSteps = [];

	var clearCurrentWorkflowSteps = function() {
		$scope.currentWorkflowSteps.length = 0;
	};

	$scope.ready = $q.all([OrganizationRepo.ready(),OrganizationCategoryRepo.ready()]);

	$scope.managedOrganization = null;

	$scope.ready.then(function() {

		$scope.updateOrganization = function(organization) {
			OrganizationRepo.update(organization).then(function() {
				//update the parent scoped selected organization
				$scope.setSelectedOrganization(organization);
			});
        };

		$scope.getManagedOrganization = function() {
			var currentOrganization = $scope.getSelectedOrganization();
			if (typeof currentOrganization !== 'undefined' && currentOrganization) {
				if (!$scope.managedOrganization || $scope.managedOrganization.id != currentOrganization.id) {
					$scope.managedOrganization = angular.copy(currentOrganization);

					clearCurrentWorkflowSteps();
					angular.forEach($scope.managedOrganization.workflowSteps, function(stepId) {

						console.log(WorkflowStepRepo.getStepById(stepId));

						WorkflowStepRepo.getStepById(stepId).then(function(step) {
							$scope.currentWorkflowSteps.push(step);
						}); 
					});					

				}
			}
			return $scope.managedOrganization;
		};

		$scope.resetManagedOrganization = function() {
			$scope.managedOrganization = angular.copy($scope.getSelectedOrganization());
		};

		//WorkflowSteps
		$scope.getWorkflowStepsByIds = function(stepIds) {
			var workflowSteps = [];

			WorkflowStepRepo.getStepsByIds(stepIds).then(function() {

			});

			return workflowSteps;
		};

	});
});
