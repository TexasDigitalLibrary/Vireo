vireo.controller("OrganizationManagementController", function ($controller, $q, $route, $scope, $timeout, AlertService, Organization, OrganizationRepo, OrganizationCategoryRepo, WorkflowStepRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationRepo = OrganizationRepo;

	OrganizationRepo.listenSelectively(function() {
		$scope.resetWorkflowSteps();
	});

	$scope.workflowStepRepo = WorkflowStepRepo;

	$scope.organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([OrganizationRepo.ready(), OrganizationCategoryRepo.ready()]);

	$scope.forms = {};

	$scope.ready.then(function() {

		$scope.resetWorkflowSteps = function() {
			$scope.organizationRepo.clearValidationResults();
			for(var key in $scope.forms) {
    			if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
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

		$scope.showOrganizationManagement = function() {
			return $scope.getSelectedOrganization().id !== undefined;
		};

		$scope.updateOrganization = function(organization) {
			organization.save().then(function() {
				// update the parent scoped selected organization
				$scope.setSelectedOrganization(organization);
			});
		};

		$scope.deleteOrganization = function(organization) {
			organization.delete().then(function(data) {
				if(data.meta.type != 'INVALID') {
					$scope.closeModal();
					$timeout(function() {
						AlertService.add(data.meta, 'organization/delete');
					}, 300);
				}
			});
		};

		$scope.cancelDeleteOrganization = function() {
			$scope.closeModal();
			$scope.getSelectedOrganization().clearValidationResults();
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

	    $scope.resetManageOrganization = function() {
			$scope.getSelectedOrganization().clearValidationResults();
			$scope.setSelectedOrganization($scope.getSelectedOrganization());
	    };

	    $scope.testBoolean = true;

	});

	$scope.acceptsSubmissions = [
        {
            "true": "Yes"
        }, {
            "false": "No"
        }
    ];

});
