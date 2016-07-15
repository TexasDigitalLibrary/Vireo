vireo.directive("textfieldform", function() {
	return {
		templateUrl: 'views/directives/textFieldForm.html',
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
		}
	};
});
