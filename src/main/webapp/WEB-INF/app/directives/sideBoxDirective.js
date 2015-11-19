vireo.directive("sidebox", function() {
	return {
		templateUrl: 'views/sidebox.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true,
		link: function ($scope, element, attr) {	    	
			$scope.heading = attr.heading;
	    }
	};
});