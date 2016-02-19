vireo.directive("textfield", function() {
	return {
		templateUrl: 'views/directives/textField.html',
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
			$scope.id = 'label-' + new Date().getTime();

			console.log($scope)
		},
		link: function($scope, element, attr) {

		}
	};
});
