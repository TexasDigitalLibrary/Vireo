vireo.controller('OrganizationSettingsController', function ($controller, $scope, $location, $routeParams, User, UserSettings,OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.newOrganization = {};

//	$scope.organizations = OrganizationRepo.get();

	$scope.organizations = [{"name":"Organization 1"},{"name":"Organization 2"}];

	$scope.createOrganization = function(organization) {
//		OrganizationRepo.add(organization);
		console.log("the org:");
		console.log(organization);
	};
});