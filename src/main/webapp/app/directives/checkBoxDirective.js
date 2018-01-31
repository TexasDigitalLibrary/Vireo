vireo.directive("checkbox", function ($controller) {
	return {
		templateUrl: 'views/directives/checkBox.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: {},
		link: function ($scope, element, attr) {
			
			if(attr.checkboxController) {
				angular.extend(this, $controller(attr.checkboxController, {$scope: $scope}));
			}

			$scope.name = attr.checkboxName;

			//$scope.checkboxModel = $scope.settings.user[attr.checkboxModel];

	    	$scope.click = function() {
	    		if(attr.checkboxNgClickFunction) {
	    			$scope[attr.checkboxNgClickFunction]($scope.name);
	    		}
	    	};

	    }
	};
});