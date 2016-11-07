vireo.controller("CustomActionSideBarController", function ($controller, $scope, $q) {
// vireo.controller("CustomActionSideBarController", function ($controller, $scope, $q, CustomActionValueCategoryRepo, CustomActionValueRepo) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	// $scope.customActionValues = CustomActionValueRepo.getAll();

	// $scope.customActionValueRepo = CustomActionValueRepo;

	// var customActionValueCategories = CustomActionValueCategoryRepo.getAll();

	console.info('TESTTESTTEST');

	// $scope.ready = $q.all([
	// 	CustomActionValueRepo.ready(),
	// 	CustomActionValueCategoryRepo.ready()
	// ]);

	// $scope.forms = {};
	
	// $scope.ready.then(function() {

		// $scope.customActionValueCategories = customActionValueCategories.filter(function (category) {
		//     return category.name != 'System';
		// });

		// $scope.reset = function() {
		// 	$scope.customActionValueRepo.clearValidationResults();
    		
    	// 	for(var key in $scope.forms) {
    	// 		if(!$scope.forms[key].$pristine) {
    	// 			$scope.forms[key].$setPristine();
    	// 		}
    	// 	}
    		
    	// 	$scope.newCustomActionValue = CustomActionValueRepo.resetNewCustomActionValue();

    	// 	if($scope.newCustomActionValue.category === undefined) {
    	// 		$scope.newCustomActionValue.category = $scope.customActionValueCategories[0];
    	// 	}

    	// 	if($scope.newCustomActionValue.parent === undefined) {
		// 		$scope.newCustomActionValue.parent = $scope.customActionValues[0];
		// 	}
		// };

		// $scope.reset();

		// $scope.createNewCustomActionValue = function(hierarchical) {
		// 	var parentCustomActionValue = hierarchical == 'true' ? CustomActionValueRepo.newCustomActionValue.parent : $scope.customActionValues[0];
		// 	CustomActionValueRepo.create({
		// 		"name": CustomActionValueRepo.newCustomActionValue.name, 
		// 		"category": CustomActionValueRepo.newCustomActionValue.category
		// 	}, parentCustomActionValue).then(function() {
		// 		$scope.reset();
		// 	});
		// };
	// });


});
