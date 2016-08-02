vireo.directive("togglebutton", function() {
	return {
		templateUrl: 'views/directives/toggleButton.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeValue": "=",
            "toggleOptions": "@",
            "toolTip": "@"
		},
		controller: function($scope) {
			$scope.setActive = function(scopeValue) {
				$scope.scopeValue = scopeValue;
			}
		},
		link: function($scope, element, attr) {

			var optionsObj = angular.fromJson($scope.toggleOptions)
			$scope.options = {};

			for(var index in optionsObj) {
				var option = optionsObj[index];
				$scope.options["option"+ index] = {
					gloss: option[Object.keys(option)[0]],
					evaluation: Object.keys(option)[0]
				}
			}
		}
	};
});
