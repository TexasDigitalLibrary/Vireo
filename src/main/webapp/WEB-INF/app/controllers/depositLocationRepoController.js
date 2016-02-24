vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.depositLocations = DepositLocationRepo.get();

	$scope.depositLocation = {};

	$scope.ready.then(function() {

		console.log($scope.depositLocations)

		$scope.createDepositLocation = function() {
			DepositLocationRepo.add($scope.depositLocation);
			$scope.depositLocation = {};
		};

		$scope.reorderDepositLocation = function(from, to) {
	    	DepositLocationRepo.reorder(from, to);
		};

	});	

});