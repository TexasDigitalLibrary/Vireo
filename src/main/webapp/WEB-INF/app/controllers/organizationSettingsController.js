vireo.controller('OrganizationSettingsController', function ($controller, $scope, $q, AccordionService, OrganizationRepo, SidebarService) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizationRepo = OrganizationRepo;

	SidebarService.addBox({
		"title": "Create Organization",
		"viewUrl": "views/sideboxes/organization.html"
	});

	$scope.organizations = OrganizationRepo.getAll();

	$scope.activeManagementPane = 'edit';
	
	$scope.newOrganization = OrganizationRepo.getNewOrganization();

	$scope.setSelectedOrganization = function(organization) {
		if(OrganizationRepo.getSelectedOrganization().id !== organization.id) {
			AccordionService.closeAll();
		}	
		OrganizationRepo.setSelectedOrganization(organization);
		$scope.newOrganization.parent = OrganizationRepo.getSelectedOrganization();
		
	};

	$scope.getSelectedOrganization = function() {
		return OrganizationRepo.getSelectedOrganization();
	};

	$scope.activateManagementPane = function(pane) {
		$scope.activeManagementPane = pane;
	};

	$scope.managementPaneIsActive = function(pane) {
		return ($scope.activeManagementPane === pane);
	}; 

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {
		$scope.newOrganization.parent = $scope.organizations[0];
	});

});
