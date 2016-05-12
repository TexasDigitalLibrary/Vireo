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
			"keyDown": "&",
			"validationResponse": "=",
			"labelWidth": "@",
			"fieldWidth": "@",
			"expanded": "="
		}
	};
});
