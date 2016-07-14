vireo.controller('NewSubmissionController', function ($controller, $scope, $location, OrganizationRepo, StudentSubmissionRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.organizations = OrganizationRepo.getAll();

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();

	$scope.setSelectedOrganization = function(organization) {
		OrganizationRepo.setSelectedOrganization(organization);
	};

	$scope.getSelectedOrganization = function() {
		return $scope.selectedOrganization;
	};

	$scope.createSubmission = function() {
		StudentSubmissionRepo.create({
			'organizationId': $scope.getSelectedOrganization().id
		}).then(function(data) {
			console.log(angular.fromJson(data.body).payload.Submission);
			$location.path("/submission/" + angular.fromJson(data.body).payload.Submission.id);
		});
	};
	
});
