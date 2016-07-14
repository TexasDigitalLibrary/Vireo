vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, OrganizationRepo, OrganizationCategoryRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationRepo = OrganizationRepo;

	OrganizationRepo.listenSelectively(function() {
		$scope.resetWorkflowSteps();
	});


	$scope.organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([OrganizationRepo.ready(), OrganizationCategoryRepo.ready()]);

	$scope.managedOrganization = null;

	$scope.ready.then(function() {

		$scope.resetWorkflowSteps = function() {
			$scope.modalData = {
				overrideable: true
			};
			$scope.closeModal();
		};

		$scope.resetWorkflowSteps();

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
					$scope.managedOrganization = currentOrganization;
				}
			}
			return $scope.managedOrganization;
		};

		$scope.resetManagedOrganization = function() {
			$scope.managedOrganization = $scope.getSelectedOrganization();
		};

		$scope.addWorkflowStep = function() {
			OrganizationRepo.addWorkflowStep($scope.modalData);
		};

		$scope.deleteWorkflowStep = function(workflowStep) {
			OrganizationRepo.deleteWorkflowStep(workflowStep);
		};
		
		$scope.updateWorkflowStep = function(workflowStep) {
			OrganizationRepo.updateWorkflowStep(workflowStep);
		};

		$scope.reorderWorkflowStepUp = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("up", workflowStepID);
		};

		$scope.reorderWorkflowStepDown = function(workflowStepID) {
			OrganizationRepo.reorderWorkflowStep("down", workflowStepID);
		};

		$scope.openConfirmDeleteModal = function(step) {
	        $scope.openModal('#workflow-step-delete-confirm-' + step.id);
	    };

	});

});
