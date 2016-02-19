vireo.directive("textfield", function() {
	return {
		templateUrl: 'views/directives/textField.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeValue": "=",
                        "toolTip": "@"
		},
		controller: function($scope) {

		},
		link: function($scope, element, attr) {

		}
	};
});
