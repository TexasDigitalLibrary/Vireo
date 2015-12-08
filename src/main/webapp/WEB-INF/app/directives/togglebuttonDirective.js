vireo.directive("togglebutton", function() {
	return {
		templateUrl: 'views/directives/toggleButton.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: {},
		controller: function($scope) {
			
			this.setActive = function(option) {
				$scope.activeButton = option;
			}

			this.optionActive = function(option) { 
				return $scope.activeButton == option;
			}
		}, 
		link: function ($scope, element, attr) {	    	
			$scope.label = attr.label;			
	    }
	};
});

vireo.directive("toggleoption", function() {
	return {
		template: '<button class="btn btn-sm btn-default" ng-click="setActive(option)" ng-class="{\'active\': optionActive(option)}" ng-transclude></button>',
		restrict: 'E',
		replace: true,
		require: '^togglebutton',
		transclude: true,
		scope: true, 
		link: function ($scope, element, attr, parent) {
			$scope.option = element.children('span').html();
			angular.extend($scope, parent);
			angular.extend($scope, attr);

		}
	}
});

