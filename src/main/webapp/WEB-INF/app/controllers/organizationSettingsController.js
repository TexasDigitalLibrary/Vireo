vireo.controller('OrganizationSettingsController', function ($controller, $scope, $q, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

	$scope.organizations = OrganizationRepo.get();

	console.log($scope.organizations);

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();

	$scope.activeManagementPane = 'edit';
	$scope.newOrganization = OrganizationRepo.getNewOrganization();

	$scope.setSelectedOrganization = function(organization) {
		OrganizationRepo.setSelectedOrganization(organization);
		$scope.newOrganization.parent = $scope.selectedOrganization;
	};

	$scope.getSelectedOrganization = function() {
		return $scope.selectedOrganization;
	};

	$scope.activateManagementPane = function(pane) {
		$scope.activeManagementPane = pane;
	};

	$scope.managementPaneIsActive = function(pane) {
		return ($scope.activeManagementPane === pane);
	}; 

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {
		$scope.newOrganization.parent = $scope.organizations.list[0];
	});

});
