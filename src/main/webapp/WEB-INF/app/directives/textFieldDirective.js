vireo.directive("textfield", function() {
	return {
		template: '<span ng-include src="view"></span>',
		restrict: 'E',
		scope: {
			"formView": "=",
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
			if($scope.formView) {
				$scope.view = 'views/directives/textFieldForm.html';
			}
			else {
				$scope.view = 'views/directives/textField.html';
			}
		}
	};
});
