vireo.directive("legendbox", function() {
	return {
		templateUrl: 'views/directives/legendBox.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true,
		link: function ($scope, element, attr) {
			$scope.heading = attr.heading;
	    }
	};
});


