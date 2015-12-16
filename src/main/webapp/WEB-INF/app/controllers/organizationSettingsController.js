vireo.controller('OrganizationSettingsController', function ($controller, $scope, $location, $routeParams, User, UserSettings,OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.newOrganization = {};
	$scope.newCategory = {};

//	$scope.organizations = OrganizationRepo.get();

	$scope.organizations = [{"name":"Organization 1"},{"name":"Organization 2"}];
	$scope.organizationCategories = [{"name":"Category 1","level":1},{"name":"Category 2","level":2}];

	$scope.createOrganization = function(organization) {
//		OrganizationRepo.add(organization);
		angular.extend($scope.organizations,[organization]);
		console.log("the org:");
		console.log(organization);
	};

	$scope.createOrganizationCategory = function(category) {
//		OrganizationCategoryRepo.add(category);
		console.log("the org category:");
		console.log(category);
		angular.extend($scope.categories,[category]);
	};
});