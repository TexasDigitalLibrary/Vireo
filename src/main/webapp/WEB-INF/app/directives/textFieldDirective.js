vireo.directive("textfield", function() {
	return {
		templateUrl: 'views/directives/textField.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeValue": "=",
			"labelWidth": "@",
			"fieldWidth": "@",
			"expanded": "="
		}
	};
});
