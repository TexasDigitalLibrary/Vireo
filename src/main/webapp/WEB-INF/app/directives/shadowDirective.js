vireo.directive("shadow", function() {
	return {
		scope: true,
		link: function ($scope, element, attr) {
			if($scope.ready !== undefined) {
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