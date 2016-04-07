vireo.controller('OrganizationSettingsController', function ($controller, $scope, $filter, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();

    $scope.panes = [1];

    $scope.shiftPanes = function(updatePaneIndex, organization) {
        
        $scope.panes[updatePaneIndex] = organization.id;

        console.log(updatePaneIndex);

        if(updatePaneIndex-1 == 0 && organization.parentOrganizations.length > 0) {               
            $scope.panes.unshift(organization.parentOrganizations[0].id);
            $scope.panes.splice(updatePaneIndex+2,1); 
        }

        if(updatePaneIndex-3 >= 0 && organization.childrenOrganizations.length > 0) {
            $scope.panes.shift();
        }
    }

    $scope.filterByParent = function(parentPaneIndex, organization) {
    	return organization.parentOrganizations.indexOf($scope.panes[parentPaneIndex]) != -1;
    }

});