vireo.controller("OrganizationSideBarController", function($controller, $scope, $q, OrganizationCategoryRepo, OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.organizations = OrganizationRepo.get();
	$scope.organizationCategories = OrganizationCategoryRepo.get();

	$scope.ready = $q.all([
		OrganizationRepo.ready(),
		OrganizationCategoryRepo.ready()
	]);
	
	$scope.ready.then(function() {

		$scope.newOrganization = OrganizationRepo.getNewOrganization();

		$scope.createNewOrganization = function() {
			OrganizationRepo.add();
		}

		$scope.findOrganizationCategoryById = function(id) {
			return OrganizationCategoryRepo.findById(id);
		}

	});


});
