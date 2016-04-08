vireo.controller('OrganizationSettingsController', function ($controller, $scope, $q, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();
    $scope.selectedOrganization;

    $scope.setSelectedOrganization = function(organization) {
    	$scope.selectedOrganization = organization;
    };

    $scope.getSelectedOrganization = function() {
    	return $scope.selectedOrganization;
    }; 

    $scope.ready = $q.all([OrganizationRepo.ready()]);

});