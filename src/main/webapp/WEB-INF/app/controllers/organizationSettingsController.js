vireo.controller('OrganizationSettingsController', function ($controller, $scope, $filter, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();

    $scope.panes = [1];

    $scope.setNextPanesParent = function(paneToUpdate, newParentId) {
        $scope.panes.splice(paneToUpdate+1,1);
        $scope.panes[paneToUpdate] = newParentId;
    }

    $scope.filterByParent = function(organization, parentPane) {
    	return organization.parentOrganizations.indexOf(parentPane) != -1;
    }

});