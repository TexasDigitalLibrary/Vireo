vireo.controller('OrganizationSettingsController', function ($controller, $scope, $location, $routeParams, User, UserSettings, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	SidebarService.addBox({"title":"Create Organization","viewUrl":"views/sideboxes/organization.html"});
});