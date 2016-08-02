vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, Organization, OrganizationRepo, OrganizationCategoryRepo, WorkflowStepRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationRepo = OrganizationRepo;

	OrganizationRepo.listenSelectively(function() {
		$scope.resetWorkflowSteps();
	});

	$scope.workflowStepRepo = WorkflowStepRepo;

	$scope.organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([OrganizationRepo.ready(), OrganizationCategoryRepo.ready()]);

	$scope.managedOrganization = null;

	$scope.forms = {};

	$scope.ready.then(function() {

		$scope.resetWorkflowSteps = function() {
			$scope.organizationRepo.clearValidationResults();
			for(var key in $scope.forms) {
    			if(!$scope.forms[key].$pristine) {
    				console.log(key)
    				console.log($scope.forms[key])
    				$scope.forms[key].$setPristine();
    			}
    		}
			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
			$scope.modalData = {
				overrideable: true
			};
			$scope.closeModal();
		};

		$scope.resetWorkflowSteps();

		$scope.updateOrganization = function(organization) {
			organization.save().then(function() {
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
			$scope.managedOrganization = $scope.getSelectedOrganization();
		};

		$scope.addWorkflowStep = function() {
			OrganizationRepo.addWorkflowStep($scope.modalData);
		};

		$scope.deleteWorkflowStep = function(workflowStep) {
			OrganizationRepo.deleteWorkflowStep(workflowStep);
		};
		
		$scope.updateWorkflowStep = function(workflowStep) {
			return OrganizationRepo.updateWorkflowStep(workflowStep);
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
