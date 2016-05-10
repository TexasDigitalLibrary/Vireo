vireo.controller('OrganizationSettingsController', function ($controller, $scope, $q, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();
    $scope.selectedOrganization = null;
    $scope.activeManagementPane = 'edit';
    $scope.organizationEditMode;
    $scope.newOrganization = OrganizationRepo.getNewOrganization();

    $scope.setSelectedOrganization = function(organization) {
    	$scope.selectedOrganization = organization;
        $scope.newOrganization.parent = organization;
    };

    $scope.getSelectedOrganization = function() {

        //console.log($scope.selectedOrganization);

    	return $scope.selectedOrganization;
    };

    $scope.activateManagementPane = function(pane) {
        $scope.activeManagementPane = pane;
    }

    $scope.managementPaneIsActive = function(pane) {
        return ($scope.activeManagementPane === pane);
    } 

    $scope.ready = $q.all([OrganizationRepo.ready()]);

    $scope.ready.then(function() {
        console.log($scope.organizations);
        $scope.newOrganization.parent = $scope.organizations.list[0];
    });

});