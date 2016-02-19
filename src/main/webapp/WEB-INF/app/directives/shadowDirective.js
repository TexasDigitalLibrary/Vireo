vireo.directive("shadow", function() {
	return {
		scope: true,
		link: function ($scope, element, attr) {
			
			//This will only work in a controller with a ready implementation
			//as seen in the Settings controller.
			//Must exist and be a promise ($$state)			
			if($scope.ready && $scope.ready.$$state) {
				$scope.ready.then(function() {
				
					var index = attr.ngModel.lastIndexOf(".");
					
					var parentObject = attr.ngModel.substring(0, index);
					
					var targetObject = attr.ngModel.substring(index + 1);
										    		
					var model = eval('$scope.' + parentObject);
					
					model['_' + targetObject] = angular.copy(model[targetObject]);

				});
			}

	    }
	};
});