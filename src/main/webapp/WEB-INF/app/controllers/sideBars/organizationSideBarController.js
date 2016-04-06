vireo.controller("OrganizationSideBarController", function($controller, $scope, $q, OrganizationCategoryRepo, OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.organizations = OrganizationRepo.get();
	$scope.organizationCategories = OrganizationCategoryRepo.get();

	$q.all([
		OrganizationRepo.ready(),
		OrganizationCategoryRepo.ready()
	]).then(function() {

		$scope.newOrganization = {};

		$scope.createNewOrganization = function() {
			OrganizationRepo.add($scope.newOrganization).then(function() {
				setForm();
			});
		}

		setForm = function() {
			$scope.newOrganization.parentId = "";
			$scope.newOrganization.categoryId = "";
			$scope.newOrganization.name = "";
		}

		setForm();

		console.log($scope.organizations);
		console.log($scope.organizationCategories);

	});


});