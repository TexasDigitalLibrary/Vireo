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
			"onBlur": "&",
			"labelWidth": "@",
			"fieldWidth": "@",
			"expanded": "="
		}
	};
});
