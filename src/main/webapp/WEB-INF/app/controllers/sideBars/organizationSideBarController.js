vireo.controller("OrganizationSideBarController", function($controller, $scope, $q, OrganizationCategoryRepo, OrganizationRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizations = OrganizationRepo.getAll();

	$scope.organizationRepo = OrganizationRepo;

	var organizationCategories = OrganizationCategoryRepo.getAll();

	$scope.ready = $q.all([
		OrganizationRepo.ready(),
		OrganizationCategoryRepo.ready()
	]);

	$scope.forms = {};
	
	$scope.ready.then(function() {

		$scope.organizationCategories = organizationCategories.filter(function (category) {
		    return category.name != 'System';
		});

		$scope.reset = function() {
			$scope.organizationRepo.clearValidationResults();
    		for(var key in $scope.forms) {
    			if(!$scope.forms[key].$pristine) {
    				$scope.forms[key].$setPristine();
    			}
    		}
    		
    		OrganizationRepo.resetNewOrganization();

			$scope.newOrganization = OrganizationRepo.getNewOrganization();

			$scope.newOrganization.category = $scope.organizationCategories[0];

			$scope.newOrganization.parent = $scope.organizations[0];
		};

		$scope.reset();

		$scope.createNewOrganization = function() {
			OrganizationRepo.create({
				"name": OrganizationRepo.newOrganization.name, 
				"category": OrganizationRepo.newOrganization.category
			},  OrganizationRepo.newOrganization.parent).then(function() {
				$scope.reset();
			});
		};

	});


});