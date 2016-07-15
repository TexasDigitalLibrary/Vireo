vireo.directive("textfield", function() {
	return {
		templateUrl: 'views/directives/textField.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeObject": "=",
			"scopeProperty": "@",
			"toolTip": "@",
			"hint": "@",
			"form": "=",
			"validations": "=",
			"onBlur": "&",
			"labelWidth": "@",
			"fieldWidth": "@",
			"expanded": "="
		},
		link: function ($scope, element, attr) {
			
			console.log($scope)

		}
	};
});
