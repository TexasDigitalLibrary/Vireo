vireo.directive("checkbox", function() {
	return {
		templateUrl: 'views/directives/checkBox.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true,
		link: function ($scope, element, attr) {
			$scope.name = attr.name;
	    }
	};
});