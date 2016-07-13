vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, Organization, OrganizationRepo, OrganizationCategoryRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([OrganizationRepo.ready(), OrganizationCategoryRepo.ready()]);

	$scope.managedOrganization = null;

	$scope.ready.then(function() {

		$scope.updateOrganization = function(organization) {
			OrganizationRepo.update(organization).then(function() {
				// update the parent scoped selected organization 
				$scope.setSelectedOrganization(organization);
			});
		};

		$scope.getManagedOrganization = function() {
			var currentOrganization = $scope.getSelectedOrganization();
			if (currentOrganization !== undefined && currentOrganization) {
				if (!$scope.managedOrganization || $scope.managedOrganization.id != currentOrganization.id) {
					$scope.managedOrganization = new Organization(currentOrganization);
				}
			}
			return $scope.managedOrganization;
		};

		$scope.resetManagedOrganization = function() {
			$scope.managedOrganization = new Organization($scope.getSelectedOrganization());
		};

		$scope.addWorkflowStep = function(newWorkflowStepName) {
			OrganizationRepo.addWorkflowStep(newWorkflowStepName).then(function() {
				angular.element("#addWorkflowStepModal").modal("hide");		
			}); 
		};

		$scope.deleteWorkflowStep = function(workflowStepID) {
			OrganizationRepo.deleteWorkflowStep(workflowStepID);
		};
		
		$scope.updateWorkflowStep = function(workflowStepToUpdate) {
			OrganizationRepo.updateWorkflowStep(workflowStepToUpdate);
		};

		$scope.reorderWorkflowStepUp = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("up", workflowStepID);
		};

		$scope.reorderWorkflowStepDown = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("down", workflowStepID);
		};

		$scope.openConfirmDeleteModal = function(id) {
	        $scope.openModal('#workflow-step-delete-confirm-' + id);
	    };

	});

});
