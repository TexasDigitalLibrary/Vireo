vireo.controller('OrganizationSettingsController', function ($controller, $scope, $filter, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();

    $scope.previouseOrganizations = [];
    $scope.openPanes = [1];

    $scope.shiftPanes = function(updatePaneIndex, organization) {

        $scope.openPanes[updatePaneIndex] = organization.id;

        console.log(updatePaneIndex);

        if(updatePaneIndex-1 == 0 && organization.parentOrganizations.indexOf(1) == -1 && organization.parentOrganizations.length > 0) {               
            $scope.openPanes.unshift($scope.previouseOrganizations[0]);
            $scope.openPanes.splice(updatePaneIndex+2,1);
        }

        if(updatePaneIndex-3 >= 0 && organization.childrenOrganizations.length > 0) {
            $scope.previouseOrganizations.unshift($scope.openPanes[0]);
            $scope.openPanes.shift();
        }

        console.log(organization);
        console.log($scope.previouseOrganizations);
        console.log($scope.openPanes);

    }

    $scope.filterByParent = function(parentPaneIndex, organization) {
    	return organization.parentOrganizations.indexOf($scope.openPanes[parentPaneIndex]) != -1;
    }

});