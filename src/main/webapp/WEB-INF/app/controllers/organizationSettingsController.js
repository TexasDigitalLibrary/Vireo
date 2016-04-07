vireo.controller('OrganizationSettingsController', function ($controller, $scope, $q, OrganizationRepo, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});

    $scope.organizations = OrganizationRepo.get();

    $scope.ready = $q.all([OrganizationRepo.ready()]);

});