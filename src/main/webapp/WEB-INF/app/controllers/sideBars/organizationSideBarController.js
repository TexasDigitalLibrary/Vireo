vireo.controller("OrganizationSideBarController", function($controller, $scope, $q, OrganizationCategoryRepo, OrganizationRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizations = OrganizationRepo.getAll();

	$scope.organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([
		OrganizationRepo.ready(),
		OrganizationCategoryRepo.ready()
	]);
	
	$scope.ready.then(function() {

		// TODO: improve on how organizations are created
		$scope.newOrganization = OrganizationRepo.getNewOrganization();

		$scope.createNewOrganization = function() {
			OrganizationRepo.create({
				"name": OrganizationRepo.newOrganization.name, 
				"category": OrganizationRepo.newOrganization.category,
				"parentOrganizationId": OrganizationRepo.newOrganization.parent.id,
			});

			OrganizationRepo.resetNewOrganization();
		};

	});


});
