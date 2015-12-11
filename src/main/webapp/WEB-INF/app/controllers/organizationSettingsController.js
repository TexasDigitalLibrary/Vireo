vireo.controller('OrganizationSettingsController', function ($controller, $scope, $location, $routeParams, User, UserSettings) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.newOrganization = {};
	$scope.createOrganization = function(organization) {
		console.log("the org:");
		console.log(organization);
	};
});