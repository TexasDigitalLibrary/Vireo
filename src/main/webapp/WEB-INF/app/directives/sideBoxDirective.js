vireo.directive("sidebox", function() {
	return {
		templateUrl: 'views/directives/sidebox.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true,
		link: function ($scope, element, attr) {	    	
			$scope.heading = attr.heading;
			$scope.clicked = '-';

			$scope.changeSymbol = function() {
				$scope.clicked = $scope.clicked=='-'? '+':'-';
			}

	    }
	};
});

vireo.directive("sideboxsub", function() {
	return {
		template: '<div class="sidebox-sub-heading" ng-transclude></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true
	};
});