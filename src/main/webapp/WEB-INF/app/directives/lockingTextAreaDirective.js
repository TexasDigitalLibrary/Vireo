vireo.directive("lockingtextarea", function() {
	return {
		templateUrl: 'views/directives/lockingTextArea.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeValue": "=",
            "toolTip": "@",
            "hint": "@",
            "onBlur": "&",
            "keyDown": "&"
		},
		controller: function($scope) {
			$scope.locked = true;

			$scope.toggleLock = function() {
				$scope.locked = !$scope.locked;
			}
		},
	};
});
